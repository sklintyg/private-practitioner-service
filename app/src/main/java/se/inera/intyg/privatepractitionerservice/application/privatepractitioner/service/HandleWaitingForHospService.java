package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleWaitingForHospService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final MailService mailService;
  private final MonitoringLogService monitoringLogService;
  private final RemovePrivatePractitionerService removePrivatePractitionerService;

  @Value("${hosp.update.email.count}")
  private int numberOfEmails;

  public void handle(PrivatePractitioner privatePractitioner) {
    if (isTimeToRemoveRegistration(privatePractitioner)) {
      removePrivatePractitionerService.remove(privatePractitioner);
    } else {
      monitoringLogService.logHospWaiting(privatePractitioner.getPersonId(),
          privatePractitioner.getHsaId());
      mailService.sendRegistrationStatusEmail(
          privatePractitioner.getRegistrationStatus(),
          privatePractitioner.getEmail()
      );
      privatePractitionerRepository.addEmailNotification(
          privatePractitioner.getPersonId(),
          LocalDateTime.now()
      );
    }
  }

  private boolean isTimeToRemoveRegistration(PrivatePractitioner privatePractitioner) {
    return privatePractitioner.getEmailCount() >= numberOfEmails;
  }
}
