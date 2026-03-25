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
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;

@Service
@RequiredArgsConstructor
public class GetHospInformationService {

  private final HospRepository hospRepository;

  public GetHospInformationResponse get(GetHospInformationRequest request) {
    if (request.getPersonId() == null || request.getPersonId().isBlank()) {
      throw new IllegalArgumentException("Person Id is required");
    }

    final var hospInformation = hospRepository.findByPersonId(request.getPersonId());

    return GetHospInformationResponse.builder()
        .personId(hospInformation.getPersonalIdentityNumber())
        .personalPrescriptionCode(hospInformation.getPersonalPrescriptionCode())
        .licensedHealthcareProfessions(
            hospInformation.getLicensedHealthcareProfessions().stream()
                .map(license -> new CodeDTO(license.code(), license.name()))
                .toList())
        .specialities(
            hospInformation.getSpecialities().stream()
                .map(speciality -> new CodeDTO(speciality.code(), speciality.name()))
                .toList())
        .build();
  }
}
