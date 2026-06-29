/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
public class MailHogUtil {

  private final RestClient restClient;
  private final JsonMapper objectMapper;

  public MailHogUtil(
      RestClient.Builder restClientBuilder, JsonMapper objectMapper, String host, int port) {
    this.objectMapper = objectMapper;
    this.restClient = restClientBuilder.baseUrl("http://%s:%s".formatted(host, port)).build();
  }

  public void reset() {
    final var deleteUrl = "/api/v1/messages";
    final var getUrl = "/api/v2/messages";

    try {
      restClient.delete().uri(deleteUrl).retrieve().toBodilessEntity();
      await()
          .atMost(Duration.ofSeconds(5))
          .pollInterval(Duration.ofMillis(150))
          .until(
              () -> {
                try {
                  return !hasMessages(getUrl, null);
                } catch (Exception _) {
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

  public void assertEmail(
      String address, String subject, String body, Integer totalExpectedMessages) {
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
      final var actualAddress = to.get("Mailbox").asString() + "@" + to.get("Domain").asString();

      final var actualSubject =
          decode(msg.path("Content").path("Headers").path("Subject").get(0).asString());

      final var actualBody =
          decodeQuotedPrintable(decode(msg.path("Content").path("Body").asString()));

      if (address.equals(actualAddress)
          && subject.equals(actualSubject)
          && actualBody.contains(body)) {
        found = true;
        break;
      }
    }

    if (!found) {
      StringBuilder errorMsg = new StringBuilder("No email found matching criteria:\n");
      errorMsg
          .append("Expected - Address: ")
          .append(address)
          .append(", Subject: ")
          .append(subject)
          .append(", Body contains: ")
          .append(body)
          .append("\n");
      errorMsg.append("Actual messages found:\n");

      for (int i = 0; i < items.size(); i++) {
        final var msg = items.get(i);
        final var to = msg.path("To").get(0);
        final var actualAddress = to.get("Mailbox").asString() + "@" + to.get("Domain").asString();
        final var actualSubject =
            decode(msg.path("Content").path("Headers").path("Subject").get(0).asString());
        final var actualBody =
            decodeQuotedPrintable(decode(msg.path("Content").path("Body").asString()));

        errorMsg
            .append("Message ")
            .append(i)
            .append(" - Address: ")
            .append(actualAddress)
            .append(", Subject: ")
            .append(actualSubject)
            .append(", Body: ")
            .append(actualBody)
            .append("\n");
      }

      throw new AssertionError(errorMsg.toString());
    }
  }

  private JsonNode getMessages(Integer expectedAmount) {
    final var requestUrl = "/api/v2/messages";

    try {
      await()
          .atMost(Duration.ofSeconds(20))
          .pollInterval(Duration.ofMillis(200))
          .until(() -> hasMessages(requestUrl, expectedAmount));

      final var res = restClient.get().uri(requestUrl).retrieve().toEntity(String.class);

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
      final var res = restClient.get().uri(requestUrl).retrieve().toEntity(String.class);
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
      final var in =
          MimeUtility.decode(
              new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), "quoted-printable");
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Could not decode quoted printable body: {}", body, e);
      return body;
    }
  }
}
