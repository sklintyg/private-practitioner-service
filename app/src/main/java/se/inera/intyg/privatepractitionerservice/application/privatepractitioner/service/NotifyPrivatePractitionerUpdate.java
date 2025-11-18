package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;

@Component
@RequiredArgsConstructor
public class NotifyPrivatePractitionerUpdate {

  private final MonitoringLogService monitoringLogService;

  public void notify(PrivatePractitioner privatePractitioner) {
    monitoringLogService.logUserDetailsChanged(
        privatePractitioner.getPersonId(),
        privatePractitioner.getHsaId()
    );
  }
}
