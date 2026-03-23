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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.POSITION_SPECIALIST_DOCTOR_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.TYPE_OF_CARE_OUTPATIENT_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.POSITION_SPECIALIST_DOCTOR;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.TYPE_OF_CARE_OUTPATIENT;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationConfigurationServiceTest {

  @Mock private CodeSystemRepository codeSystemRepository;
  @InjectMocks private RegistrationConfigurationService registrationConfigurationService;

  @BeforeEach
  void setUp() {
    when(codeSystemRepository.getHealthcareServiceTypeCodes())
        .thenReturn(List.of(HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE));
    when(codeSystemRepository.getPositionCodes()).thenReturn(List.of(POSITION_SPECIALIST_DOCTOR));
    when(codeSystemRepository.getTypeOfCareCodes()).thenReturn(List.of(TYPE_OF_CARE_OUTPATIENT));
  }

  @Test
  void shouldReturnHealthcareServiceTypesCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getHealthcareServiceTypeCodes().size());
    assertEquals(
        HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE_DTO,
        actual.getHealthcareServiceTypeCodes().getFirst());
  }

  @Test
  void shouldReturnPositionCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getPositionCodes().size());
    assertEquals(POSITION_SPECIALIST_DOCTOR_DTO, actual.getPositionCodes().getFirst());
  }

  @Test
  void shouldReturnTypeOfCareCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getTypeOfCareCodes().size());
    assertEquals(TYPE_OF_CARE_OUTPATIENT_DTO, actual.getTypeOfCareCodes().getFirst());
  }
}
