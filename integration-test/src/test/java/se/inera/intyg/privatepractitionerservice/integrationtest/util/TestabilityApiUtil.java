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

import static se.inera.intyg.privatepractitionerservice.integrationtest.util.PrivatePractitionerUtil.privatePractitionerPersonId;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;

@Slf4j
@RequiredArgsConstructor
public class TestabilityApiUtil {

  private final RestTestClient restClient;
  private static final List<String> privatePractitionerPersonIds = new ArrayList<>();

  public ResponseEntity<PrivatePractitionerDTO> addPrivatePractitioner(
      TestabilityCreateRegistrationRequest request) {
    final var result =
        restClient
            .post()
            .uri("/testability/privatepractitioner")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectBody(PrivatePractitionerDTO.class)
            .returnResult();

    final var response = toResponseEntity(result);

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

    final var result =
        restClient
            .delete()
            .uri("/testability/clear")
            .exchange()
            .expectBody(Void.class)
            .returnResult();

    if (result.getStatus() != HttpStatus.OK) {
      log.error(
          "Could not clear practitioners using testability! StatusCode: '%s'"
              .formatted(result.getStatus()));
    }
    privatePractitionerPersonIds.clear();
  }

  private static <T> ResponseEntity<T> toResponseEntity(EntityExchangeResult<T> result) {
    return ResponseEntity.status(result.getStatus()).body(result.getResponseBody());
  }
}
