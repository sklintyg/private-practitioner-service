package se.inera.intyg.privatepractitionerservice.testability.service;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_PROFILE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.testability.service.converter.TestabilityPrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.testability.service.factory.TestabilityPrivatePractitionerFactory;

@Profile(TESTABILITY_PROFILE)
@Service
@Transactional
@RequiredArgsConstructor
public class TestabilityPrivatePractitionerService {

  private final TestabilityPrivatePractitionerFactory privatePractitionerFactory;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final TestabilityPrivatePractitionerConverter privatePractitionerConverter;

  public PrivatePractitionerDTO createRegistration(
      TestabilityCreateRegistrationRequest registration) {

    final var privatePractitioner = privatePractitionerFactory.create(registration);

    final var savedPrivatePractitioner = privatePractitionerRepository.save(privatePractitioner);

    return privatePractitionerConverter.convert(savedPrivatePractitioner);
  }

  public void reset(List<String> personIds) {
    privatePractitionerRepository.reset(personIds);
  }
}


