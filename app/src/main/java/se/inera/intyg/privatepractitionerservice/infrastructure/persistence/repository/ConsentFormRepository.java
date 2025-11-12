package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.ConsentForm;

@Repository
@RequiredArgsConstructor
public class ConsentFormRepository {

  private final MedgivandeTextEntityRepository medgivandeTextEntityRepository;

  public ConsentForm current() {
    final var latest = medgivandeTextEntityRepository.findLatest();
    return new ConsentForm(
        latest.getVersion(),
        latest.getMedgivandeText()
    );
  }
}
