/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.privatepractitionerservice.application.internalapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se.inera.intyg.privatepractitionerservice.application.service.PrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.PrivatePractitionerController;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.PrivatePractitionerDto;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.services.IntegrationService;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerControllerTest {

  private static final String PERSONAL_IDENTITY_NUMBER = "191212121212";

  @Mock
  private PrivatePractitionerService privatePractitionerService;

  @Mock
  private IntegrationService integrationService;

  @InjectMocks
  private PrivatePractitionerController privatePractitionerController;

  @Test
  void getPrivatePractitioners_ok() {
    String hsaId1 = "SE123-1";
    String hsaId2 = "SE123-2";
    String personId = PERSONAL_IDENTITY_NUMBER;
    String name = "My Name";
    String careprovider = "My Careprovider";
    String email = "my@email.com";
    LocalDateTime registrationDate = LocalDateTime.now();

    when(privatePractitionerService.getPrivatePractitioners())
        .thenReturn(List.of(
            new PrivatePractitioner(hsaId1, personId, name, careprovider, email, registrationDate),
            new PrivatePractitioner(hsaId2, personId, name, careprovider, email,
                registrationDate)));

    ResponseEntity<List<PrivatePractitionerDto>> privatePractitionersResponse = privatePractitionerController.getPrivatePractitioners();

    assertNotNull(privatePractitionersResponse);
    assertSame(HttpStatus.OK, privatePractitionersResponse.getStatusCode());
    assertEquals(2, privatePractitionersResponse.getBody().size());
  }

  @Test
  void getPrivatePractitioners_empty_ok() {

    ResponseEntity<List<PrivatePractitionerDto>> privatePractitionersResponse = privatePractitionerController.getPrivatePractitioners();

    assertNotNull(privatePractitionersResponse);
    assertSame(HttpStatus.OK, privatePractitionersResponse.getStatusCode());
    assertTrue(privatePractitionersResponse.getBody().isEmpty());
  }

  @Test
  void getPrivatePractitioner_hsaid_ok() {
    String hsaID = "SE123";
    String name = "My Name";
    String careprovider = "My Careprovider";
    String email = "my@email.com";
    LocalDateTime registrationDate = LocalDateTime.now();

    when(privatePractitionerService.getPrivatePractitioner(hsaID))
        .thenReturn(
            new PrivatePractitioner(hsaID, PERSONAL_IDENTITY_NUMBER, name, careprovider, email,
                registrationDate));

    ResponseEntity<PrivatePractitionerDto> privatePractitionerResponse = privatePractitionerController.getPrivatePractitioner(
        hsaID);

    assertNotNull(privatePractitionerResponse);
    assertSame(HttpStatus.OK, privatePractitionerResponse.getStatusCode());
    PrivatePractitionerDto privatePractitioner = privatePractitionerResponse.getBody();
    assertNotNull(privatePractitioner);
    assertEquals(hsaID, privatePractitioner.getHsaId());
    assertEquals(name, privatePractitioner.getName());
    assertEquals(careprovider, privatePractitioner.getCareproviderName());
    assertEquals(email, privatePractitioner.getEmail());
    assertEquals(registrationDate, privatePractitioner.getRegistrationDate());
  }

  @Test
  void getPrivatePractitioner_personId_ok() {
    String hsaID = "SE123";
    String name = "My Name";
    String careprovider = "My Careprovider";
    String email = "my@email.com";
    LocalDateTime registrationDate = LocalDateTime.now();

    when(privatePractitionerService.getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER))
        .thenReturn(
            new PrivatePractitioner(hsaID, PERSONAL_IDENTITY_NUMBER, name, careprovider, email,
                registrationDate));

    ResponseEntity<PrivatePractitionerDto> privatePractitionerResponse = privatePractitionerController
        .getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER);

    assertNotNull(privatePractitionerResponse);
    assertSame(HttpStatus.OK, privatePractitionerResponse.getStatusCode());
    PrivatePractitionerDto privatePractitioner = privatePractitionerResponse.getBody();
    assertNotNull(privatePractitioner);
    assertEquals(hsaID, privatePractitioner.getHsaId());
    assertEquals(PERSONAL_IDENTITY_NUMBER, privatePractitioner.getPersonId());
    assertEquals(name, privatePractitioner.getName());
    assertEquals(careprovider, privatePractitioner.getCareproviderName());
    assertEquals(email, privatePractitioner.getEmail());
    assertEquals(registrationDate, privatePractitioner.getRegistrationDate());
  }

  @Test
  void getPrivatePractitioner_notFound() {
    when(privatePractitionerService.getPrivatePractitioner(anyString())).thenReturn(null);

    ResponseEntity<PrivatePractitionerDto> notFoundResponse = privatePractitionerController.getPrivatePractitioner(
        "notFound");

    assertNotNull(notFoundResponse);
    assertSame(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());

  }

  @Test
  void getPrivatePractitioner_missingPersonOrHsaId() {
    ResponseEntity<PrivatePractitionerDto> notFoundResponse = privatePractitionerController.getPrivatePractitioner(
        null);

    assertNotNull(notFoundResponse);
    assertSame(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
  }

  @Test
  void validatePrivatePractitioner() {
    var validatePrivatePractitionerResponse = new ValidatePrivatePractitionerResponse();
    when(integrationService.validatePrivatePractitionerByPersonId(anyString())).thenReturn(
        validatePrivatePractitionerResponse);

    ValidatePrivatePractitionerRequest validatePrivatePractitionerRequest = new ValidatePrivatePractitionerRequest(
        PERSONAL_IDENTITY_NUMBER);
    var response = privatePractitionerController.validatePrivatePractitioner(
        validatePrivatePractitionerRequest);

    verify(integrationService).validatePrivatePractitionerByPersonId(PERSONAL_IDENTITY_NUMBER);
    assertNotNull(response);
  }
}
