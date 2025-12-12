package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdateHospService {

  private final HospRepository hospRepository;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  private final HandleWaitingForHospService handleWaitingForHospService;

  @Transactional
  public void update() {
    if (!hospRepository.needUpdateFromHosp()) {
      return;
    }

    final var privatePractitioners = privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate();
    privatePractitioners.forEach(privatePractitioner -> {

          final var hospPerson = hospRepository.findByPersonId(privatePractitioner.getPersonId());
          privatePractitioner.updateWithHospInformation(hospPerson);
          privatePractitionerRepository.save(privatePractitioner);

          hospRepository.addToCertifier(privatePractitioner);

          switch (privatePractitioner.getRegistrationStatus()) {
            case AUTHORIZED -> notifyPrivatePractitionerRegistration.notify(privatePractitioner);
            case WAITING_FOR_HOSP, NOT_AUTHORIZED ->
                handleWaitingForHospService.handle(privatePractitioner);
          }
        }
    );

    hospRepository.hospUpdated();
  }
}
