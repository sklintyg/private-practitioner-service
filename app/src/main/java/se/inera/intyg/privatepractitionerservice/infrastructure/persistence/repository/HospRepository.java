package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HospRepository {

  private final HospService hospService;
  private final HashUtility hashUtility;

  public void addToCertifier(PrivatePractitioner privatePractitioner) {
    final var result = hospService.handleHospCertificationPersonResponseType(
        privatePractitioner.getHsaId(),
        "add",
        privatePractitioner.getPersonId(),
        null
    );

    log.info("Registered for '{}' certifier in HOSP with resultCode: '{}' and resultText '{}'",
        privatePractitioner.getHsaId(), result.getResultCode(), result.getResultText()
    );
  }

  public void removeFromCertifier() {

  }

  public Optional<HospPerson> findByPersonId(String personId) {
    final var response = hospService.getHospCredentialsForPersonResponseType(personId);

    if (response == null || response.getPersonalIdentityNumber() == null) {
      log.info("No hosp person found for '{}'", hashUtility.hash(personId));
      return Optional.empty();
    }

    return Optional.of(
        HospPerson.builder()
            .personalIdentityNumber(response.getPersonalIdentityNumber())
            .personalPrescriptionCode(response.getPersonalPrescriptionCode())
            .licensedHealthcareProfessions(
                response.getHealthCareProfessionalLicence() == null ? List.of() :
                    response.getHealthCareProfessionalLicence().stream()
                        .map(license -> new LicensedHealtcareProfession(
                            license.getHealthCareProfessionalLicenceCode(),
                            license.getHealthCareProfessionalLicenceName()
                        ))
                        .toList()
            )
            .specialities(
                response.getHealthCareProfessionalLicenceSpeciality() == null ? List.of() :
                    response.getHealthCareProfessionalLicenceSpeciality().stream()
                        .map(code -> new Speciality(
                            code.getSpecialityCode(),
                            code.getSpecialityName()
                        ))
                        .toList()
            )
            .build()
    );
  }

  public Optional<HospPerson> updatedHospPerson(PrivatePractitioner privatePractitioner) {
    final var hospLastUpdate = hospService.getHospLastUpdate();
    if (!privatePractitioner.needHospUpdate(hospLastUpdate)) {
      log.info("No update needed for hosp person '{}'",
          hashUtility.hash(privatePractitioner.getPersonId())
      );
      return Optional.empty();
    }

    return findByPersonId(privatePractitioner.getPersonId())
        .map(person -> person.withHospUpdated(hospLastUpdate))
        .or(() -> Optional.of(
                HospPerson.builder()
                    .personalIdentityNumber(privatePractitioner.getPersonId())
                    .hospUpdated(hospLastUpdate)
                    .build()
            )
        );
  }
}
