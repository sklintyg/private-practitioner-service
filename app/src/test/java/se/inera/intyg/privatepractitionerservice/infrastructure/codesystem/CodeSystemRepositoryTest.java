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
package se.inera.intyg.privatepractitionerservice.infrastructure.codesystem;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeSystemRepositoryTest {

  @InjectMocks private CodeSystemRepository codeSystemRepository;

  @Test
  void shouldReturnHealthcareServiceTypeCodes() {
    final var actual = codeSystemRepository.getHealthcareServiceTypeCodes();
    assertAll(
        () -> assertNotNull(actual, "HealthcareServiceType codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "HealthcareServiceType codes should not be empty"));
  }

  @Test
  void shouldReturnFalseWhenHealthcareServiceTypeCodeDoesntExists() {
    assertFalse(codeSystemRepository.healthcareServiceTypeExists("xxx"));
  }

  @Test
  void shouldReturnTrueWhenHealthcareServiceTypeCodeExists() {
    assertTrue(codeSystemRepository.healthcareServiceTypeExists("10"));
  }

  @Test
  void shouldReturnPositionCodes() {
    final var actual = codeSystemRepository.getPositionCodes();
    assertAll(
        () -> assertNotNull(actual, "Position codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "Position codes should not be empty"));
  }

  @Test
  void shouldReturnFalseWhenPositionCodeDoesntExists() {
    assertFalse(codeSystemRepository.positionExists("xxx"));
  }

  @Test
  void shouldReturnTrueWhenPositionCodeExists() {
    assertTrue(codeSystemRepository.positionExists("201010"));
  }

  @Test
  void shouldReturnTypeOfCareCodes() {
    final var actual = codeSystemRepository.getTypeOfCareCodes();
    assertAll(
        () -> assertNotNull(actual, "TypeOfCare codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "TypeOfCare codes should not be empty"));
  }

  @Test
  void shouldReturnFalseWhenTypeOfCareCodeDoesntExists() {
    assertFalse(codeSystemRepository.typeOfCareExists("xxx"));
  }

  @Test
  void shouldReturnTrueWhenTypeOfCareCodeExists() {
    assertTrue(codeSystemRepository.typeOfCareExists("01"));
  }
}
