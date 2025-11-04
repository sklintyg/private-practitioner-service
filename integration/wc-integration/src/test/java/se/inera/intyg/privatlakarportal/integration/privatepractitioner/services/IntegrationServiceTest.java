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
package se.inera.intyg.privatlakarportal.integration.privatepractitioner.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitioner.dto.ValidatePrivatePractitionerResultCode;
import se.inera.intyg.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.intyg.privatlakarportal.common.service.DateHelperService;
import se.inera.intyg.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.intyg.privatlakarportal.logging.HashUtility;
import se.inera.intyg.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerResponseType;
import se.riv.infrastructure.directory.privatepractitioner.v1.HoSPersonType;
import se.riv.infrastructure.directory.privatepractitioner.v1.ResultCodeEnum;

/**
 * Created by pebe on 2015-08-18.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntegrationServiceTest {

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

  @Before
  public void setup() throws IOException {
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

    when(privatlakareRepository.findByHsaId(GODKAND_HSA_ID)).thenReturn(privatlakare);
    when(privatlakareRepository.findByPersonId(GODKAND_PERSON_ID)).thenReturn(privatlakare);
    when(privatlakareRepository.findByHsaId(FINNS_EJ_HSA_ID)).thenReturn(null);
    when(privatlakareRepository.findByPersonId(FINNS_EJ_PERSON_ID)).thenReturn(null);
    when(privatlakareRepository.findByPersonId(INVALID_PERSON_ID)).thenReturn(null);
    when(privatlakareRepository.findByPersonId(EJ_GODKAND_PERSON_ID)).thenReturn(
        privatlakareEjGodkand);
    when(privatlakareRepository.findByPersonId(EJ_LAKARE_PERSON_ID)).thenReturn(null);

    when(dateHelperService.now()).thenReturn(LocalDate.parse("2015-09-09").atStartOfDay());
  }

  @Test
  public void testGetPrivatePractitionerByHsaId() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(
        GODKAND_HSA_ID);
    assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  public void testGetPrivatePractitionerByPersonId() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        GODKAND_PERSON_ID);
    assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  public void testGetPrivatePractitionerByHsaIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByHsaId(
        FINNS_EJ_HSA_ID);
    assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
  }

  @Test
  public void testGetPrivatePractitionerByPersonIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        FINNS_EJ_PERSON_ID);
    assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(FINNS_EJ_PERSON_ID));
  }

  @Test
  public void testGetPrivatePractitionerByInvalidPersonIdNonExisting() {
    GetPrivatePractitionerResponseType response = integrationService.getPrivatePractitionerByPersonId(
        INVALID_PERSON_ID);
    assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(INVALID_PERSON_ID));
  }

  @Test
  public void testValidatePrivatePractitionerByPersonId() {
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
  public void testValidatePrivatePractitionerByPersonIdFirstLogin() {
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
  public void testValidatePrivatePractitionerByPersonIdEjGodkand() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        EJ_GODKAND_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP,
        response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(EJ_GODKAND_PERSON_ID));
  }

  @Test
  public void testValidatePrivatePractitionerByPersonIdEjLakare() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        EJ_LAKARE_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(EJ_LAKARE_PERSON_ID));
  }

  @Test
  public void testValidatePrivatePractitionerByPersonIdNonExisting() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        FINNS_EJ_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(FINNS_EJ_PERSON_ID));
  }

  @Test
  public void testValidatePrivatePractitionerByInvalidPersonIdNonExisting() {
    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        INVALID_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.NO_ACCOUNT, response.getResultCode());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(INVALID_PERSON_ID));
  }
}
