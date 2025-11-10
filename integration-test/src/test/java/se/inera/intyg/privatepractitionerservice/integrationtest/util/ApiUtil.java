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
import se.inera.intyg.privatepractitionerservice.testability.dto.PrivatlakareDto;


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

  public ResponseEntity<PrivatlakareDto> testabilityGetRegistration() {
    final var requestUrl = "http://localhost:" + port + "/api/test/registration/191212121212";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.GET,
        new HttpEntity<>(null, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );
  }

  public ResponseEntity<PrivatlakareDto> testabilityRemoveRegistration() {
    final var requestUrl = "http://localhost:" + port + "/api/test/registration/191212121212";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.DELETE,
        new HttpEntity<>(null, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );
  }

}
