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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CodeDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.RegistrationConfigurationResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;

@Service
@RequiredArgsConstructor
public class RegistrationConfigurationService {

  private final CodeSystemRepository codeSystemRepository;

  public RegistrationConfigurationResponse get() {
    return RegistrationConfigurationResponse.builder()
        .healthcareServiceTypeCodes(
            codeSystemRepository.getHealthcareServiceTypeCodes().stream()
                .map(
                    healthcareServiceType ->
                        new CodeDTO(healthcareServiceType.code(), healthcareServiceType.name()))
                .toList())
        .positionCodes(
            codeSystemRepository.getPositionCodes().stream()
                .map(position -> new CodeDTO(position.code(), position.name()))
                .toList())
        .typeOfCareCodes(
            codeSystemRepository.getTypeOfCareCodes().stream()
                .map(typeOfCare -> new CodeDTO(typeOfCare.code(), typeOfCare.name()))
                .toList())
        .build();
  }
}
