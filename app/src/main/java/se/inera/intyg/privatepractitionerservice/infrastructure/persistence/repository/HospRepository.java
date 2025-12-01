package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.HospPersonConverter;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HospRepository {

  private final HospService hospService;
  private final HashUtility hashUtility;
  private final HospPersonConverter hospPersonConverter;

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
    return hospPersonConverter.convert(response);
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
