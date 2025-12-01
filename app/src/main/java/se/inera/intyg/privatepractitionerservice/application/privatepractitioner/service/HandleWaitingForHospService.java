package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleWaitingForHospService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HospRepository hospRepository;
  private final MailService mailService;
  private final MonitoringLogService monitoringLogService;

  @Value("${hosp.update.email.interval}")
  private long mailInterval;

  @Value("${hosp.update.email.count}")
  private int numberOfEmails;

  public void handle(PrivatePractitioner privatePractitioner) {
    if (isTimeToRemoveRegistration(privatePractitioner.getRegistrationDate())) {
      removeRegistration(privatePractitioner);
    }

    if (isTimeToNotifyAboutAwaitingHospStatus(privatePractitioner.getPersonId())) {
      mailService.sendRegistrationStatusEmail(
          privatePractitioner.getRegistrationStatus(),
          privatePractitioner.getEmail()
      );
      privatePractitionerRepository.addEmailNotification(
          privatePractitioner.getPersonId(), LocalDateTime.now()
      );
    }
  }

  private boolean isTimeToRemoveRegistration(LocalDateTime registrationDate) {
    return MINUTES.between(registrationDate, LocalDateTime.now()) >= mailInterval * numberOfEmails;
  }

  private void removeRegistration(PrivatePractitioner privatePractitioner) {
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

  private boolean isTimeToNotifyAboutAwaitingHospStatus(String personId) {
    final var emailNotifications = privatePractitionerRepository.getEmailNotifications(personId);
    return emailNotifications.isTimeToNotify(Duration.of(mailInterval, MINUTES), numberOfEmails);
  }
}
