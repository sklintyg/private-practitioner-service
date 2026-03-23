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
    final var response =
        this.restTemplate.exchange(
            requestUrl,
            HttpMethod.POST,
            new HttpEntity<>(request, headers),
            new ParameterizedTypeReference<PrivatePractitionerDTO>() {},
            Collections.emptyMap());

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      final var body = response.getBody();
      TestabilityApiUtil.addPrivatePractitionerPersonId(body.getPersonId());
    }

    return response;
  }

  public ResponseEntity<PrivatePractitionerDTO> erasePrivatePractitioner(String hsaId) {
    final var requestUrl =
        "http://localhost:" + port + "/internalapi/privatepractitioner/%s".formatted(hsaId);
    final var headers = new HttpHeaders();
    final var response =
        this.restTemplate.exchange(
            requestUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<PrivatePractitionerDTO>() {},
            Collections.emptyMap());
    return response;
  }

  public ResponseEntity<PrivatePractitionerDTO> updatePrivatePractitioner(
      UpdatePrivatePractitionerRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.PUT,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {},
        Collections.emptyMap());
  }

  public ResponseEntity<GetHospInformationResponse> hospInformation(
      GetHospInformationRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner/hosp";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {},
        Collections.emptyMap());
  }

  public ResponseEntity<RegistrationConfigurationResponse> registrationConfiguration() {
    final var requestUrl =
        "http://localhost:" + port + "/internalapi/privatepractitioner/configuration";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.GET,
        new HttpEntity<>(null, headers),
        new ParameterizedTypeReference<>() {},
        Collections.emptyMap());
  }

  public ResponseEntity<ValidatePrivatePractitionerResponse> validatePrivatePractitioner(
      ValidatePrivatePractitionerRequest request) {
    final var requestUrl = "http://localhost:" + port + "/internalapi/privatepractitioner/validate";
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        new ParameterizedTypeReference<>() {},
        Collections.emptyMap());
  }

  public ResponseEntity<PrivatePractitionerDTO> getPrivatePractitioner(String personOrHsaId) {
    final var requestUrl =
        "http://localhost:"
            + port
            + "/internalapi/privatepractitioner?personOrHsaId="
            + personOrHsaId;
    final var headers = new HttpHeaders();
    return this.restTemplate.exchange(
        requestUrl,
        HttpMethod.GET,
        new HttpEntity<>(null, headers),
        new ParameterizedTypeReference<>() {},
        Collections.emptyMap());
  }
}
