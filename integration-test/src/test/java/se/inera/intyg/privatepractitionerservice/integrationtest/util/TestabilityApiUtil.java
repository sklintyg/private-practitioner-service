package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import static se.inera.intyg.privatepractitionerservice.integrationtest.util.PrivatePractitionerUtil.privatePractitionerPersonId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;

@Slf4j
@RequiredArgsConstructor
public class TestabilityApiUtil {

  private final TestRestTemplate restTemplate;
  private final int port;
  private static final List<String> privatePractitionerPersonIds = new ArrayList<>();

  public ResponseEntity<PrivatePractitionerDTO> addPrivatePractitioner(
      TestabilityCreateRegistrationRequest request) {
    final var requestUrl = "http://localhost:%s/testability/privatepractitioner".formatted(port);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final ResponseEntity<PrivatePractitionerDTO> response = this.restTemplate.exchange(
        requestUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );

    if (privatePractitionerPersonId(response.getBody()) != null) {
      privatePractitionerPersonIds.add(privatePractitionerPersonId(response.getBody()));
    }
    return response;
  }

  public static void addPrivatePractitionerPersonId(String personId) {
    privatePractitionerPersonIds.add(personId);
  }

  public void reset() {
    if (privatePractitionerPersonIds.isEmpty()) {
      return;
    }

    final var requestUrl = "http://localhost:%s/testability/privatepractitioner".formatted(port);
    final var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    final ResponseEntity<Void> response = this.restTemplate.exchange(
        requestUrl,
        HttpMethod.DELETE,
        new HttpEntity<>(privatePractitionerPersonIds, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );

    if (response.getStatusCode() != HttpStatus.OK) {
      log.error(
          "Could not reset testability with request '%s'! StatusCode: '%s'".formatted(
              requestUrl,
              response.getStatusCode()
          )
      );
    }
    privatePractitionerPersonIds.clear();
  }
}
