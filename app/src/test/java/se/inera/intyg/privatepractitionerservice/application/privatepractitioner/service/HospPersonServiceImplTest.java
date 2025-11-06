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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@ExtendWith(MockitoExtension.class)
class HospPersonServiceImplTest {

  private static final String VALID_PERSON_ID = "1912121212";
  private static final String INVALID_PERSON_ID = "0000000000";
  private static final String CERTIFIER_ID = "CERTIFIER_0001";
  private static final String REASON = "Test";
  private static final String ADD = "add";
  private static final String REMOVE = "remove";

  @Mock
  HospService authorizationManagementService;

  @InjectMocks
  HospPersonServiceImpl hospPersonService;

  @BeforeEach
  void setupExpectations() {

    HospCredentialsForPerson response = new HospCredentialsForPerson();

    response.setPersonalIdentityNumber(VALID_PERSON_ID);
    response.setPersonalPrescriptionCode(CERTIFIER_ID);

    lenient().when(authorizationManagementService.getHospCredentialsForPersonResponseType(
        VALID_PERSON_ID)).thenReturn(response);

    lenient().when(authorizationManagementService.getHospCredentialsForPersonResponseType(
        INVALID_PERSON_ID)).thenReturn(null);
  }

  @Test
  void testGetHsaPersonInfoWithValidPerson() {

    HospPerson res = hospPersonService.getHospPerson(VALID_PERSON_ID);

    assertNotNull(res);
  }

  @Test
  void testGetHsaPersonInfoWithInvalidPerson() {

    HospPerson res = hospPersonService.getHospPerson(INVALID_PERSON_ID);

    assertNull(res);
  }


  @Test
  void testAddToCertifier() {
    Result response = new Result();
    response.setResultText("OK");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(anyString(),
        anyString(), anyString(), isNull())).thenReturn(response);

    hospPersonService.addToCertifier(VALID_PERSON_ID, CERTIFIER_ID);

    verify(authorizationManagementService).handleHospCertificationPersonResponseType(CERTIFIER_ID,
        ADD, VALID_PERSON_ID, null);
  }

  @Test
  void testRemoveFromCertifier() {

    Result response = new Result();
    response.setResultText("OK");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(anyString(),
        anyString(), anyString(), anyString())).thenReturn(response);

    hospPersonService.removeFromCertifier(VALID_PERSON_ID, CERTIFIER_ID, REASON);

    verify(authorizationManagementService).handleHospCertificationPersonResponseType(CERTIFIER_ID,
        REMOVE, VALID_PERSON_ID, REASON);
  }

  @Test
  void testAddToCertifierTrue() {
    Result result = new Result();
    result.setResultCode("OK");
    result.setResultText("Det blev bra.");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(CERTIFIER_ID, ADD,
        VALID_PERSON_ID, null)).thenReturn(result);

    assertTrue(hospPersonService.addToCertifier(VALID_PERSON_ID, CERTIFIER_ID));
    verify(authorizationManagementService, times(1)).handleHospCertificationPersonResponseType(
        CERTIFIER_ID, ADD, VALID_PERSON_ID, null);
  }

  @Test
  void testAddToCertifierFalse() {
    Result result = new Result();
    result.setResultCode("Fail");
    result.setResultText("Det blev inte bra.");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(CERTIFIER_ID, ADD,
        VALID_PERSON_ID, null)).thenReturn(result);

    assertFalse(hospPersonService.addToCertifier(VALID_PERSON_ID, CERTIFIER_ID));
    verify(authorizationManagementService, times(1)).handleHospCertificationPersonResponseType(
        CERTIFIER_ID, ADD, VALID_PERSON_ID, null);
  }


  @Test
  void testRemoveFromCertifierTrue() {
    Result result = new Result();
    result.setResultCode("OK");
    result.setResultText("Det blev bra.");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(CERTIFIER_ID,
        REMOVE, VALID_PERSON_ID, REASON)).thenReturn(result);

    assertTrue(hospPersonService.removeFromCertifier(VALID_PERSON_ID, CERTIFIER_ID, REASON));
    verify(authorizationManagementService, times(1)).handleHospCertificationPersonResponseType(
        CERTIFIER_ID, REMOVE, VALID_PERSON_ID, REASON);
  }

  @Test
  void testRemoveFromCertifierFalse() {
    Result result = new Result();
    result.setResultCode("Fail");
    result.setResultText("Det blev inte bra.");
    when(authorizationManagementService.handleHospCertificationPersonResponseType(CERTIFIER_ID,
        REMOVE, VALID_PERSON_ID, REASON)).thenReturn(result);

    assertFalse(hospPersonService.removeFromCertifier(VALID_PERSON_ID, CERTIFIER_ID, REASON));
    verify(authorizationManagementService, times(1)).handleHospCertificationPersonResponseType(
        CERTIFIER_ID, REMOVE, VALID_PERSON_ID, REASON);
  }
}
