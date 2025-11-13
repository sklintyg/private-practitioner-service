package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotifyPrivatePractitionerRegistration {

  private final MonitoringLogService monitoringLogService;
  private final MailService mailService;

  public void notify(PrivatePractitioner privatePractitioner) {
    final var registrationStatus = privatePractitioner.getRegistrationStatus();
    switch (registrationStatus) {
      case AUTHORIZED -> monitoringLogService.logUserAuthorizedInHosp(
          privatePractitioner.getPersonId(),
          privatePractitioner.getHsaId()
      );
      case WAITING_FOR_HOSP -> monitoringLogService.logHospWaiting(
          privatePractitioner.getPersonId(),
          privatePractitioner.getHsaId()
      );
      case NOT_AUTHORIZED -> monitoringLogService.logUserNotAuthorizedInHosp(
          privatePractitioner.getPersonId(),
          privatePractitioner.getHsaId()
      );
      default -> log.info("Registration status {} does not trigger any monitoring log.",
          registrationStatus);
    }

    mailService.sendRegistrationStatusEmail(registrationStatus, privatePractitioner.getEmail());

    monitoringLogService.logUserRegistered(
        privatePractitioner.getPersonId(),
        "N/A",
        privatePractitioner.getHsaId(),
        registrationStatus
    );
  }
}
