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

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
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

  private final RestTestClient restClient;

  public ResponseEntity<PrivatePractitionerDTO> registerPrivatePractitioner(
      CreateRegistrationRequest request) {
    final var result =
        restClient
            .post()
            .uri("/internalapi/privatepractitioner")
            .body(request)
            .exchange()
            .expectBody(PrivatePractitionerDTO.class)
            .returnResult();

    final var response = toResponseEntity(result);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      TestabilityApiUtil.addPrivatePractitionerPersonId(response.getBody().getPersonId());
    }

    return response;
  }

  public ResponseEntity<PrivatePractitionerDTO> erasePrivatePractitioner(String hsaId) {
    final var result =
        restClient
            .delete()
            .uri("/internalapi/privatepractitioner/{hsaId}", hsaId)
            .exchange()
            .expectBody(PrivatePractitionerDTO.class)
            .returnResult();

    return toResponseEntity(result);
  }

  public ResponseEntity<PrivatePractitionerDTO> updatePrivatePractitioner(
      UpdatePrivatePractitionerRequest request) {
    final var result =
        restClient
            .put()
            .uri("/internalapi/privatepractitioner")
            .body(request)
            .exchange()
            .expectBody(PrivatePractitionerDTO.class)
            .returnResult();

    return toResponseEntity(result);
  }

  public ResponseEntity<GetHospInformationResponse> hospInformation(
      GetHospInformationRequest request) {
    final var result =
        restClient
            .post()
            .uri("/internalapi/privatepractitioner/hosp")
            .body(request)
            .exchange()
            .expectBody(GetHospInformationResponse.class)
            .returnResult();

    return toResponseEntity(result);
  }

  public ResponseEntity<RegistrationConfigurationResponse> registrationConfiguration() {
    final var result =
        restClient
            .get()
            .uri("/internalapi/privatepractitioner/configuration")
            .exchange()
            .expectBody(RegistrationConfigurationResponse.class)
            .returnResult();

    return toResponseEntity(result);
  }

  public ResponseEntity<ValidatePrivatePractitionerResponse> validatePrivatePractitioner(
      ValidatePrivatePractitionerRequest request) {
    final var result =
        restClient
            .post()
            .uri("/internalapi/privatepractitioner/validate")
            .body(request)
            .exchange()
            .expectBody(ValidatePrivatePractitionerResponse.class)
            .returnResult();

    return toResponseEntity(result);
  }

  public ResponseEntity<PrivatePractitionerDTO> getPrivatePractitioner(String personOrHsaId) {
    final var result =
        restClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/internalapi/privatepractitioner")
                        .queryParam("personOrHsaId", personOrHsaId)
                        .build())
            .exchange()
            .expectBody(PrivatePractitionerDTO.class)
            .returnResult();

    return toResponseEntity(result);
  }

  private static <T> ResponseEntity<T> toResponseEntity(EntityExchangeResult<T> result) {
    return ResponseEntity.status(result.getStatus()).body(result.getResponseBody());
  }
}
