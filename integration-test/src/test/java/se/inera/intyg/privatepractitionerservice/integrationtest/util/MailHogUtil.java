package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RequiredArgsConstructor
public class MailHogUtil {

  private final TestRestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final String host;
  private final int port;

  public void reset() {
    final var deleteUrl = "http://%s:%s/api/v1/messages".formatted(host, port);
    final var getUrl = "http://%s:%s/api/v2/messages".formatted(host, port);

    try {
      restTemplate.delete(deleteUrl);
      await()
          .atMost(Duration.ofSeconds(5))
          .pollInterval(Duration.ofMillis(150))
          .until(() -> {
            try {
              return !hasMessages(getUrl, null);
            } catch (Exception e) {
              return false;
            }
          });
    } catch (Exception e) {
      log.warn("Failed to reset MailHog messages", e);
    }
  }

  public void assertEmail(String address, String subject, String body) {
    assertEmail(address, subject, body, null);
  }

  public void assertEmail(String address, String subject, String body,
      Integer totalExpectedMessages) {
    final var messages = getMessages(totalExpectedMessages);

    if (messages == null) {
      throw new IllegalStateException("No messages received within timeout period");
    }

    final var items = messages.path("items");
    if (totalExpectedMessages != null) {
      assertEquals(totalExpectedMessages, items.size());
    }

    boolean found = false;

    for (int i = 0; i < items.size(); i++) {
      final var msg = items.get(i);
      final var to = msg.path("To").get(0);
      final var actualAddress = to.get("Mailbox").asText() + "@" + to.get("Domain").asText();

      final var actualSubject = decode(
          msg
              .path("Content")
              .path("Headers")
              .path("Subject")
              .get(0)
              .asText()
      );

      final var actualBody =
          decodeQuotedPrintable(
              decode(
                  msg
                      .path("Content")
                      .path("Body")
                      .asText()
              )
          );

      if (address.equals(actualAddress) &&
          subject.equals(actualSubject) &&
          actualBody.contains(body)) {
        found = true;
        break;
      }
    }

    if (!found) {
      StringBuilder errorMsg = new StringBuilder("No email found matching criteria:\n");
      errorMsg.append("Expected - Address: ").append(address)
          .append(", Subject: ").append(subject)
          .append(", Body contains: ").append(body)
          .append("\n");
      errorMsg.append("Actual messages found:\n");

      for (int i = 0; i < items.size(); i++) {
        final var msg = items.get(i);
        final var to = msg.path("To").get(0);
        final var actualAddress = to.get("Mailbox").asText() + "@" + to.get("Domain").asText();
        final var actualSubject = decode(
            msg.path("Content").path("Headers").path("Subject").get(0).asText()
        );
        final var actualBody = decodeQuotedPrintable(
            decode(msg.path("Content").path("Body").asText())
        );

        errorMsg.append("Message ").append(i).append(" - Address: ").append(actualAddress)
            .append(", Subject: ").append(actualSubject)
            .append(", Body: ").append(actualBody).append("\n");
      }

      throw new AssertionError(errorMsg.toString());
    }
  }

  private JsonNode getMessages(Integer expectedAmount) {
    final var requestUrl = "http://%s:%s/api/v2/messages".formatted(host, port);

    try {
      await()
          .atMost(Duration.ofSeconds(20))
          .pollInterval(Duration.ofMillis(200))
          .until(() -> hasMessages(requestUrl, expectedAmount));

      final ResponseEntity<String> res = restTemplate.getForEntity(requestUrl, String.class);

      if (res.getStatusCode() != HttpStatus.OK) {
        log.warn("Failed to retrieve messages from MailHog. Status: {}", res.getStatusCode());
        return null;
      }

      return objectMapper.readTree(res.getBody());
    } catch (Exception e) {
      log.warn("No messages received within timeout or error occurred", e);
      return null;
    }
  }

  private boolean hasMessages(String requestUrl, Integer expectedAmount) {
    try {
      final var res = restTemplate.getForEntity(requestUrl, String.class);
      if (res.getStatusCode() == HttpStatus.OK && res.getBody() != null) {
        final var messages = objectMapper.readTree(res.getBody());
        final var total = messages.path("total").asInt(0);
        return expectedAmount == null ? total > 0 : total == expectedAmount;
      }
      return false;
    } catch (Exception e) {
      log.warn("Error checking for messages!", e);
      return false;
    }
  }

  private String decode(String encoded) {
    try {
      return MimeUtility.decodeText(encoded);
    } catch (Exception e) {
      log.warn("Could not decode text: {}", encoded, e);
      return encoded;
    }
  }

  private static String decodeQuotedPrintable(String body) {
    try {
      final var in = MimeUtility.decode(
          new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)),
          "quoted-printable"
      );
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Could not decode quoted printable body: {}", body, e);
      return body;
    }
  }
}
