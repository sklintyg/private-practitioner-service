package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Restriction;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;

@Slf4j
@RequiredArgsConstructor
@Component
public class HospPersonConverter {

  private final HashUtility hashUtility;

  public Optional<HospPerson> convert(HospCredentialsForPerson hospCredentialsForPerson) {
    if (hospCredentialsForPerson == null
        || hospCredentialsForPerson.getPersonalIdentityNumber() == null) {
      return Optional.empty();
    }

    return Optional.of(
        HospPerson.builder()
            .personalIdentityNumber(hospCredentialsForPerson.getPersonalIdentityNumber())
            .personalPrescriptionCode(hospCredentialsForPerson.getPersonalPrescriptionCode())
            .licensedHealthcareProfessions(convertHealthcareProfessions(hospCredentialsForPerson))
            .specialities(convertSpecialities(hospCredentialsForPerson))
            .restrictions(convertRestrictions(hospCredentialsForPerson))
            .build()
    );
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
        .filter(restrictionDTO -> {
          if (restrictionDTO == null || !restrictionDTO.isValid()) {
            log.info(
                "Invalid restriction type returned from hosp for personId '{}'",
                hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
            );
            return false;
          }
          return true;
        })
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
        .filter(healthCareProfessionalLicence -> {
          if (healthCareProfessionalLicence == null
              || !healthCareProfessionalLicence.isValid()) {
            log.info(
                "Invalid healthCareProfessionalLicence returned from hosp for personId '{}'",
                hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
            );
            return false;
          }
          return true;
        })
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
        .filter(speciality -> {
          if (speciality == null || !speciality.isValid()) {
            log.info(
                "Invalid HCPSpecialityCode returned from hosp for personId '{}'",
                hashUtility.hash(hospCredentialsForPerson.getPersonalIdentityNumber())
            );
            return false;
          }
          return true;
        })
        .filter(HCPSpecialityCodes::isPhysicianSpeciality)
        .map(speciality -> new Speciality(
            speciality.getSpecialityCode(),
            speciality.getSpecialityName()
        )).toList();
  }
}

