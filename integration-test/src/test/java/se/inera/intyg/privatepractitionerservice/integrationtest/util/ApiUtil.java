package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;


@RequiredArgsConstructor
public class ApiUtil {

  private final TestRestTemplate restTemplate;
  private final int port;

  public ResponseEntity<PrivatePractitionerDTO> registerPrivatePractitioner(
      CreateRegistrationRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner";
    System.out.println("Request URL: " + requestUrl);
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );
  }
}
