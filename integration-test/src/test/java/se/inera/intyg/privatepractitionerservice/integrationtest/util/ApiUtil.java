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
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.RegistrationConfigurationResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;


@RequiredArgsConstructor
public class ApiUtil {

  private final TestRestTemplate restTemplate;
  private final int port;

  public ResponseEntity<PrivatePractitionerDTO> registerPrivatePractitioner(
      CreateRegistrationRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner";
    final var headers = new HttpHeaders();
    final var response = this.restTemplate.exchange(
        requestUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<PrivatePractitionerDTO>() {
        },
        Collections.emptyMap()
    );

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      final var body = response.getBody();
      TestabilityApiUtil.addPrivatePractitionerPersonId(body.getPersonId());
    }

    return response;
  }

  public ResponseEntity<PrivatePractitionerDTO> updatePrivatePractitioner(
      UpdatePrivatePractitionerRequest request
  ) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.PUT,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {
        },
        Collections.emptyMap()
    );
  }

  public ResponseEntity<GetHospInformationResponse> hospInformation(
      GetHospInformationRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner/hosp";
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

  public ResponseEntity<RegistrationConfigurationResponse> registrationConfiguration() {
    final var requestUrl =
        "http://localhost:" + port + "/internalapi/privatepractitioner/configuration";
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

  public ResponseEntity<ValidatePrivatePractitionerResponse> validatePrivatePractitioner(
      ValidatePrivatePractitionerRequest request) {
    final var requestUrl =
        "http://localhost:" + port + "/internalapi/privatepractitioner/validate";
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

  public ResponseEntity<PrivatePractitionerDTO> getPrivatePractitioner(String personOrHsaId) {
    final var requestUrl =
        "http://localhost:" + port + "/internalapi/privatepractitioner?personOrHsaId="
            + personOrHsaId;
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
}
