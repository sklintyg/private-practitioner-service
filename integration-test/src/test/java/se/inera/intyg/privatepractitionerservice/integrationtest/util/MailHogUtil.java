package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  private final int port;

  public void reset() {
    final var requestUrl = "http://localhost:%s/api/v1/messages".formatted(port);

    try {
      restTemplate.delete(requestUrl);
    } catch (Exception ex) {
      log.warn("Could not reset MailHog messages", ex);
    }
  }

  public void assertEmail(String address, String subject, String body) {
    final var messages = getMessages();

    if (messages == null) {
      throw new IllegalStateException("No messages received within timeout period");
    }

    final var msg = messages.path("items").get(0);
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

    assertAll(
        () -> assertEquals(address, actualAddress),
        () -> assertEquals(subject, actualSubject),
        () -> assertTrue(actualBody.contains(body),
            () -> "Body was '%s' when expected to contain '%s'".formatted(actualBody, body)
        )
    );
  }

  private JsonNode getMessages() {
    final var requestUrl = "http://localhost:%s/api/v2/messages".formatted(port);

    try {
      await()
          .atMost(Duration.ofSeconds(5))
          .pollInterval(Duration.ofMillis(200))
          .until(() -> hasMessages(requestUrl));

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

  private boolean hasMessages(String requestUrl) {
    try {
      final var res = restTemplate.getForEntity(requestUrl, String.class);
      if (res.getStatusCode() == HttpStatus.OK && res.getBody() != null) {
        final var messages = objectMapper.readTree(res.getBody());
        final var total = messages.path("total").asInt(0);
        return total > 0;
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
