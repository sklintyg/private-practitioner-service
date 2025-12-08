package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.HospPersonConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.HospUppdateringEntity;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HospRepository {

  private final HospService hospService;
  private final HashUtility hashUtility;
  private final HospPersonConverter hospPersonConverter;
  private final HospUppdateringEntityRepository hospUppdateringEntityRepository;

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

  // TODO: Implement removal in HOSP
  public boolean removeFromCertifier(PrivatePractitioner privatePractitioner, String reason) {
    return false;
  }

  public HospPerson findByPersonId(String personId) {
    final var response = hospService.getHospCredentialsForPersonResponseType(personId);
    final var hospPerson = hospPersonConverter.convert(response);

    return StringUtils.hasText(hospPerson.getPersonalIdentityNumber())
        ? hospPerson.withHospUpdated(hospService.getHospLastUpdate())
        : HospPerson.builder()
            .personalIdentityNumber(personId)
            .hospUpdated(hospService.getHospLastUpdate())
            .build();
  }

  public Optional<HospPerson> updatedHospPerson(PrivatePractitioner privatePractitioner) {
    final var hospLastUpdate = hospService.getHospLastUpdate();
    if (!privatePractitioner.needHospUpdate(hospLastUpdate)) {
      log.info("No update needed for hosp person '{}'",
          hashUtility.hash(privatePractitioner.getPersonId())
      );
      return Optional.empty();
    }

    return Optional.of(findByPersonId(privatePractitioner.getPersonId()));
  }

  public boolean needUpdateFromHosp() {
    final var hospLastUpdate = hospService.getHospLastUpdate();
    if (hospLastUpdate == null) {
      log.warn("Hosp last update is null, cannot determine if update is needed");
      return false;
    }
    return hospUppdateringEntityRepository.findHospUppdatering()
        .map(hospUppdateringEntity ->
            hospUppdateringEntity.getSenasteHospUppdatering().isBefore(hospLastUpdate)
        )
        .orElse(true);
  }

  public void hospUpdated() {
    final var hospLastUpdate = hospService.getHospLastUpdate();
    if (hospLastUpdate == null) {
      log.warn("Hosp last update is null, cannot update stored hosp update");
      return;
    }
    final var hospUppdateringEntity = hospUppdateringEntityRepository.findHospUppdatering()
        .orElse(new HospUppdateringEntity());
    hospUppdateringEntity.setSenasteHospUppdatering(hospLastUpdate);
    hospUppdateringEntityRepository.save(hospUppdateringEntity);
  }
}
