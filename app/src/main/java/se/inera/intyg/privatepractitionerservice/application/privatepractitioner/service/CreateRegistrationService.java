package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerFactory;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.CreateRegistrationRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateRegistrationService {

  private final CreateRegistrationRequestValidator createRegistrationRequestValidator;
  private final PrivatePractitionerFactory privatePractitionerFactory;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HospRepository hospRepository;
  private final NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  private final PrivatePractitionerConverter privatePractitionerConverter;

  @Transactional
  public PrivatePractitionerDTO createRegistration(CreateRegistrationRequest registration) {
    createRegistrationRequestValidator.validate(registration);

    final var privatePractitioner = privatePractitionerFactory.create(registration);

    final var hospPerson = hospRepository.findByPersonId(privatePractitioner.getPersonId());
    hospPerson.ifPresent(privatePractitioner::updateWithHospInformation);

    final var savedPrivatePractitioner = privatePractitionerRepository.save(privatePractitioner);

    hospRepository.addToCertifier(savedPrivatePractitioner);

    notifyPrivatePractitionerRegistration.notify(savedPrivatePractitioner);

    return privatePractitionerConverter.convert(savedPrivatePractitioner);
  }
}
