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
package se.inera.intyg.privatepractitionerservice.testability.service.converter;

import java.util.List;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CodeDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;

@Component
public class TestabilityPrivatePractitionerConverter {

  public PrivatePractitionerDTO convert(PrivatePractitioner privatePractitioner) {
    if (privatePractitioner == null) {
      return null;
    }

    return PrivatePractitionerDTO.builder()
        .hsaId(privatePractitioner.getHsaId())
        .personId(privatePractitioner.getPersonId())
        .name(privatePractitioner.getName())
        .email(privatePractitioner.getEmail())
        .careProviderName(privatePractitioner.getCareProviderName())
        .registrationDate(privatePractitioner.getRegistrationDate())
        .position(privatePractitioner.getPosition())
        .careUnitName(privatePractitioner.getCareUnitName())
        .healthcareServiceType(privatePractitioner.getHealthcareServiceType())
        .typeOfCare(privatePractitioner.getTypeOfCare())
        .workplaceCode(privatePractitioner.getWorkplaceCode())
        .phoneNumber(privatePractitioner.getPhoneNumber())
        .address(privatePractitioner.getAddress())
        .zipCode(privatePractitioner.getZipCode())
        .city(privatePractitioner.getCity())
        .municipality(privatePractitioner.getMunicipality())
        .county(privatePractitioner.getCounty())
        .personalPrescriptionCode(privatePractitioner.getPersonalPrescriptionCode())
        .specialties(convertSpecialities(privatePractitioner.getSpecialties()))
        .licensedHealthcareProfessions(
            convertLicensed(privatePractitioner.getLicensedHealthcareProfessions()))
        .build();
  }

  private List<CodeDTO> convertSpecialities(List<Speciality> specialities) {
    if (specialities == null) {
      return List.of();
    }

    return specialities.stream().map(s -> new CodeDTO(s.code(), s.name())).toList();
  }

  private List<CodeDTO> convertLicensed(List<LicensedHealtcareProfession> licensed) {
    if (licensed == null) {
      return List.of();
    }

    return licensed.stream().map(l -> new CodeDTO(l.code(), l.name())).toList();
  }
}
