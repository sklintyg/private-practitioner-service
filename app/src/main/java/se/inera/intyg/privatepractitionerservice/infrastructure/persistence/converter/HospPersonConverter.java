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

  private List<Restriction> convertRestrictions(
      HospCredentialsForPerson hospCredentialsForPerson) {
    if (hospCredentialsForPerson.getRestrictions() == null) {
      log.info(
          "Null Restrictions value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
      );
      return List.of();
    }

    return hospCredentialsForPerson.getRestrictions().stream()
        .map(RestrictionDTO::validate)
        .map(restriction -> new Restriction(
            restriction.getRestrictionCode(),
            restriction.getRestrictionName(),
            restriction.getHealthCareProfessionalLicenceCode()
        ))
        .toList();
  }

  private List<LicensedHealtcareProfession> convertHealthcareProfessions(
      HospCredentialsForPerson hospCredentialsForPerson) {

    if (hospCredentialsForPerson.getHealthCareProfessionalLicence() == null) {
      log.info(
          "Null HealthCareProfessionalLicence value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
      );
      return List.of();
    }

    return hospCredentialsForPerson.getHealthCareProfessionalLicence().stream()
        .map(HealthCareProfessionalLicence::validate)
        .map(healthCareProfessionalLicence -> new LicensedHealtcareProfession(
            healthCareProfessionalLicence.getHealthCareProfessionalLicenceCode(),
            healthCareProfessionalLicence.getHealthCareProfessionalLicenceName()
        ))
        .toList();
  }

  private List<Speciality> convertSpecialities(HospCredentialsForPerson hospCredentialsForPerson) {

    if (hospCredentialsForPerson.getHealthCareProfessionalLicenceSpeciality() == null) {
      log.info(
          "Null HealthCareProfessionalLicenceSpeciality value received from hosp for personId '{}'",
          hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
      );
      return List.of();
    }

    return hospCredentialsForPerson.getHealthCareProfessionalLicenceSpeciality().stream()
        .map(HCPSpecialityCodes::validate)
        .map(speciality -> new Speciality(
            speciality.getSpecialityCode(),
            speciality.getSpecialityName(),
            speciality.getHealthCareProfessionalLicenceCode()))
        .toList();
  }
}

