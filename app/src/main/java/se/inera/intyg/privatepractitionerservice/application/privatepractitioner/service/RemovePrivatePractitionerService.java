package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemovePrivatePractitionerService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HospRepository hospRepository;
  private final MailService mailService;
  private final MonitoringLogService monitoringLogService;

  public void remove(PrivatePractitioner privatePractitioner) {
    if (!hospRepository.removeFromCertifier(privatePractitioner, "remove")) {
      log.warn(
          "Could not remove private practitioner '{}' from certifier! Will not remove registration.",
          privatePractitioner.getHsaId());
      return;
    }
    privatePractitionerRepository.remove(privatePractitioner);
    mailService.sendRegistrationRemovedEmail(privatePractitioner.getEmail());
    monitoringLogService.logRegistrationRemoved(
        privatePractitioner.getPersonId(),
        privatePractitioner.getHsaId()
    );

  }
}
