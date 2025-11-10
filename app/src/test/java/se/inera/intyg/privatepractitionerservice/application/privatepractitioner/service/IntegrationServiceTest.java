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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertFalse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.HoSPersonDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ResultCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode;
import se.inera.intyg.privatepractitionerservice.infrastructure.config.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;

/**
 * Created by pebe on 2015-08-18.
 */
@ExtendWith(MockitoExtension.class)
class IntegrationServiceTest {

  @Mock
  private PrivatlakareEntityRepository privatlakareEntityRepository;

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

  private HoSPersonDTO verifyHosPerson;

  @BeforeEach
  void setup() throws IOException {
    ReflectionTestUtils.setField(hashUtility, "salt", "salt");
    ObjectMapper objectMapper = new CustomObjectMapper();

    Resource res = new ClassPathResource("IntegrationServiceTest/test_Privatlakare.json");
    PrivatlakareEntity privatlakareEntity = objectMapper.readValue(res.getInputStream(),
        PrivatlakareEntity.class);

    PrivatlakareEntity privatlakareEntityEjGodkand = objectMapper.readValue(res.getInputStream(),
        PrivatlakareEntity.class);
    privatlakareEntityEjGodkand.setGodkandAnvandare(false);

    PrivatlakareEntity privatlakareEntityEjLakare = objectMapper.readValue(res.getInputStream(),
        PrivatlakareEntity.class);
    final List<LegitimeradYrkesgruppEntity> legitimeradYrkesgrupperEntity = new ArrayList<>();
    legitimeradYrkesgrupperEntity.add(new LegitimeradYrkesgruppEntity("Dietist", "DT"));
    privatlakareEntityEjLakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupperEntity);

    res = new ClassPathResource("IntegrationServiceTest/test_HosPerson.json");
    verifyHosPerson = objectMapper.readValue(res.getInputStream(), HoSPersonDTO.class);

    lenient().when(privatlakareEntityRepository.findByHsaId(GODKAND_HSA_ID)).thenReturn(
        privatlakareEntity);
    lenient().when(privatlakareEntityRepository.findByPersonId(GODKAND_PERSON_ID))
        .thenReturn(privatlakareEntity);
    lenient().when(privatlakareEntityRepository.findByHsaId(FINNS_EJ_HSA_ID)).thenReturn(null);
    lenient().when(privatlakareEntityRepository.findByPersonId(FINNS_EJ_PERSON_ID))
        .thenReturn(null);
    lenient().when(privatlakareEntityRepository.findByPersonId(INVALID_PERSON_ID)).thenReturn(null);
    lenient().when(privatlakareEntityRepository.findByPersonId(EJ_GODKAND_PERSON_ID)).thenReturn(
        privatlakareEntityEjGodkand);
    lenient().when(privatlakareEntityRepository.findByPersonId(EJ_LAKARE_PERSON_ID))
        .thenReturn(null);

    lenient().when(dateHelperService.now())
        .thenReturn(LocalDate.parse("2015-09-09").atStartOfDay());
  }

  @Test
  void testGetPrivatePractitionerByHsaId() {
    final var response = integrationService.getPrivatePractitionerByHsaId(GODKAND_HSA_ID);
    Assertions.assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    org.assertj.core.api.Assertions.assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  void testGetPrivatePractitionerByPersonId() {
    final var response = integrationService.getPrivatePractitionerByPersonId(GODKAND_PERSON_ID);
    Assertions.assertEquals(ResultCodeEnum.OK, response.getResultCode());
    assertNotNull(response.getHoSPerson());
    org.assertj.core.api.Assertions.assertThat(response.getHoSPerson())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyHosPerson);
  }

  @Test
  void testGetPrivatePractitionerByHsaIdNonExisting() {
    final var response = integrationService.getPrivatePractitionerByHsaId(FINNS_EJ_HSA_ID);
    Assertions.assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
  }

  @Test
  void testGetPrivatePractitionerByPersonIdNonExisting() {
    final var response = integrationService.getPrivatePractitionerByPersonId(FINNS_EJ_PERSON_ID);
    Assertions.assertEquals(ResultCodeEnum.ERROR, response.getResultCode());
    assertNull(response.getHoSPerson());
    assertFalse("The personId must be hashed and not displayed in clear text.",
        response.getResultText().contains(FINNS_EJ_PERSON_ID));
  }

  @Test
  void testGetPrivatePractitionerByInvalidPersonIdNonExisting() {
    final var response = integrationService.getPrivatePractitionerByPersonId(INVALID_PERSON_ID);
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
    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByHsaId(
        GODKAND_HSA_ID);
    assertEquals(verifyHosPerson.getEnhet().getStartdatum(),
        privatlakareEntity.getEnhetStartdatum());
    assertEquals(verifyHosPerson.getEnhet().getVardgivare().getStartdatum(),
        privatlakareEntity.getVardgivareStartdatum());
    // HospUpdateService should be called to verify that privatlakare still has lakarbehorighet
    verify(hospUpdateService).checkForUpdatedHospInformation(privatlakareEntity);
  }

  @Test
  void testValidatePrivatePractitionerByPersonIdFirstLogin() {
    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByHsaId(
        GODKAND_HSA_ID);
    privatlakareEntity.setEnhetStartdatum(null);
    privatlakareEntity.setVardgivareStartdatum(null);

    ValidatePrivatePractitionerResponse response = integrationService.validatePrivatePractitionerByPersonId(
        GODKAND_PERSON_ID);
    assertEquals(ValidatePrivatePractitionerResultCode.OK, response.getResultCode());

    // Startdates should be updated to current time
    assertNotNull(privatlakareEntity.getEnhetStartdatum());
    assertNotNull(privatlakareEntity.getVardgivareStartdatum());
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
