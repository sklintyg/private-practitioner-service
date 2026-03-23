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
package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Restriction;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson.RestrictionDTO;

@Slf4j
@RequiredArgsConstructor
@Component
public class HospPersonConverter {

  private final HashUtility hashUtility;

  public HospPerson convert(@NonNull HospCredentialsForPerson hospCredentialsForPerson) {
    if (hospCredentialsForPerson.getPersonalIdentityNumber() == null) {
      return HospPerson.builder().build();
    }

    return HospPerson.builder()
        .personalIdentityNumber(hospCredentialsForPerson.getPersonalIdentityNumber())
        .personalPrescriptionCode(hospCredentialsForPerson.getPersonalPrescriptionCode())
        .licensedHealthcareProfessions(convertHealthcareProfessions(hospCredentialsForPerson))
        .specialities(convertSpecialities(hospCredentialsForPerson))
        .restrictions(convertRestrictions(hospCredentialsForPerson))
        .build();
  }

  private List<Restriction> convertRestrictions(HospCredentialsForPerson hospCredentialsForPerson) {
    if (hospCredentialsForPerson.getRestrictions() == null) {
      log.info(
          "Null Restrictions value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber()));
      return List.of();
    }

    return hospCredentialsForPerson.getRestrictions().stream()
        .map(RestrictionDTO::validate)
        .map(
            restriction ->
                new Restriction(
                    restriction.getRestrictionCode(),
                    restriction.getRestrictionName(),
                    restriction.getHealthCareProfessionalLicenceCode()))
        .toList();
  }

  private List<LicensedHealtcareProfession> convertHealthcareProfessions(
      HospCredentialsForPerson hospCredentialsForPerson) {

    if (hospCredentialsForPerson.getHealthCareProfessionalLicence() == null) {
      log.info(
          "Null HealthCareProfessionalLicence value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber()));
      return List.of();
    }

    return hospCredentialsForPerson.getHealthCareProfessionalLicence().stream()
        .map(HealthCareProfessionalLicence::validate)
        .map(
            healthCareProfessionalLicence ->
                new LicensedHealtcareProfession(
                    healthCareProfessionalLicence.getHealthCareProfessionalLicenceCode(),
                    healthCareProfessionalLicence.getHealthCareProfessionalLicenceName()))
        .toList();
  }

  private List<Speciality> convertSpecialities(HospCredentialsForPerson hospCredentialsForPerson) {

    if (hospCredentialsForPerson.getHealthCareProfessionalLicenceSpeciality() == null) {
      log.info(
          "Null HealthCareProfessionalLicenceSpeciality value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber()));
      return List.of();
    }

    return hospCredentialsForPerson.getHealthCareProfessionalLicenceSpeciality().stream()
        .map(HCPSpecialityCodes::validate)
        .map(
            speciality ->
                new Speciality(
                    speciality.getSpecialityCode(),
                    speciality.getSpecialityName(),
                    speciality.getHealthCareProfessionalLicenceCode()))
        .toList();
  }
}
