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
package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertFalse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitioner.dto.ValidatePrivatePractitionerResultCode;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.hsa.services.HospUpdateService;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.json.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.services.IntegrationServiceImpl;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.LegitimeradYrkesgrupp;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Privatlakare;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.service.DateHelperService;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.v1.HoSPersonType;
import se.riv.infrastructure.directory.privatepractitioner.v1.ResultCodeEnum;

/**
 * Created by pebe on 2015-08-18.
 */
@ExtendWith(MockitoExtension.class)
class IntegrationServiceTest {

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @Mock
  private HospUpdateService hospUpdateService;

  @Mock
  private DateHelperService dateHelperService;

  @Spy
  private HashUtility hashUtility;

  @InjectMocks
  private IntegrationServiceImpl integrationService;

  private static final String EJ_GODKAND_PERSON_ID = "191212121212";
  private static final String EJ_LAKARE_PERSON_ID = "201212121212";
  private static final String GODKAND_HSA_ID = "existingHsaId";
  private static final String GODKAND_PERSON_ID = "192011189228";
  private static final String FINNS_EJ_HSA_ID = "nonExistingHsaId";
  private static final String FINNS_EJ_PERSON_ID = "196705053723";
  private static final String INVALID_PERSON_ID = "XXYYZZAABBCC";

  private HoSPersonType verifyHosPerson;

  @BeforeEach
  void setup() throws IOException {
    ReflectionTestUtils.setField(hashUtility, "salt", "salt");
    ObjectMapper objectMapper = new CustomObjectMapper();

    Resource res = new ClassPathResource("IntegrationServiceTest/test_Privatlakare.json");
    Privatlakare privatlakare = objectMapper.readValue(res.getInputStream(), Privatlakare.class);

    Privatlakare privatlakareEjGodkand = objectMapper.readValue(res.getInputStream(),
        Privatlakare.class);
    privatlakareEjGodkand.setGodkandAnvandare(false);

    Privatlakare privatlakareEjLakare = objectMapper.readValue(res.getInputStream(),
        Privatlakare.class);
    Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<>();
    legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Dietist", "DT"));
    privatlakareEjLakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);

    res = new ClassPathResource("IntegrationServiceTest/test_HosPerson.json");
    verifyHosPerson = objectMapper.readValue(res.getInputStream(), HoSPersonType.class);

    lenient().when(privatlakareRepository.findByHsaId(GODKAND_HSA_ID)).thenReturn(privatlakare);
    lenient().when(privatlakareRepository.findByPersonId(GODKAND_PERSON_ID))
        .thenReturn(privatlakare);
    lenient().when(privatlakareRepository.findByHsaId(FINNS_EJ_HSA_ID)).thenReturn(null);
    lenient().when(privatlakareRepository.findByPersonId(FINNS_EJ_PERSON_ID)).thenReturn(null);
    lenient().when(privatlakareRepository.findByPersonId(INVALID_PERSON_ID)).thenReturn(null);
    lenient().when(privatlakareRepository.findByPersonId(EJ_GODKAND_PERSON_ID)).thenReturn(
        privatlakareEjGodkand);
    lenient().when(privatlakareRepository.findByPersonId(EJ_LAKARE_PERSON_ID)).thenReturn(null);

    lenient().when(dateHelperService.now())
        .thenReturn(LocalDate.parse("2015-09-09").atStartOfDay());
  }

  @Test
  void testGetPrivatePractitionerByHsaId() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(
        GODKAND_HSA_ID);
    Assertions.assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    org.assertj.core.api.Assertions.assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  void testGetPrivatePractitionerByPersonId() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        GODKAND_PERSON_ID);
    Assertions.assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    org.assertj.core.api.Assertions.assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  void testGetPrivatePractitionerByHsaIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(
        FINNS_EJ_HSA_ID);
    Assertions.assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
  }

  @Test
  void testGetPrivatePractitionerByPersonIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        FINNS_EJ_PERSON_ID);
    Assertions.assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(FINNS_EJ_PERSON_ID));
  }

  @Test
  void testGetPrivatePractitionerByInvalidPersonIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        INVALID_PERSON_ID);
    Assertions.assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(INVALID_PERSON_ID));
  }

  @Test
  void testValidatePrivatePractitionerByPersonId() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        GODKAND_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.OK, response.getResultCode());

    // Startdates should NOT be updated to current time
    Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
    assertEquals(verifyHosPerson.getEnhet().getStartdatum(), privatlakare.getEnhetStartdatum());
    assertEquals(verifyHosPerson.getEnhet().getVardgivare().getStartdatum(),
        privatlakare.getVardgivareStartdatum());
    // HospUpdateService should be called to verify that privatlakare still has lakarbehorighet
    verify(hospUpdateService).checkForUpdatedHospInformation(privatlakare);
  }

  @Test
  void testValidatePrivatePractitionerByPersonIdFirstLogin() {
    Privatlakare privatlakare = privatlakareRepository.findByHsaId(GODKAND_HSA_ID);
    privatlakare.setEnhetStartdatum(null);
    privatlakare.setVardgivareStartdatum(null);

    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        GODKAND_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.OK, response.getResultCode());

    // Startdates should be updated to current time
    assertNotNull(privatlakare.getEnhetStartdatum());
    assertNotNull(privatlakare.getVardgivareStartdatum());
  }

  @Test
  void testValidatePrivatePractitionerByPersonIdEjGodkand() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        EJ_GODKAND_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP,
        response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(EJ_GODKAND_PERSON_ID));
  }

  @Test
  void testValidatePrivatePractitionerByPersonIdEjLakare() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        EJ_LAKARE_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(EJ_LAKARE_PERSON_ID));
  }

  @Test
  void testValidatePrivatePractitionerByPersonIdNonExisting() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        FINNS_EJ_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(FINNS_EJ_PERSON_ID));
  }

  @Test
  void testValidatePrivatePractitionerByInvalidPersonIdNonExisting() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        INVALID_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(INVALID_PERSON_ID));
  }
}
