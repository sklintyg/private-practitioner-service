package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.mockito.Mockito.verify;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;

@ExtendWith(MockitoExtension.class)
class NotifyPrivatePractitionerRegistrationTest {

  @Mock
  private MonitoringLogService monitoringLogService;
  @Mock
  private MailService mailService;
  @InjectMocks
  private NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;

  @Test
  void shouldMonitorLogUserAuthorized() {
    notifyPrivatePractitionerRegistration.notify(kranstegeBuilder().build());

    verify(monitoringLogService).logUserAuthorizedInHosp(DR_KRANSTEGE_PERSON_ID,
        DR_KRANSTEGE_HSA_ID);
  }

  @Test
  void shouldMonitorLogUserWaitingForHosp() {
    final var kranstegeWaitingForHosp = kranstegeBuilder()
        .licensedHealthcareProfessions(List.of())
        .build();

    notifyPrivatePractitionerRegistration.notify(kranstegeWaitingForHosp);

    verify(monitoringLogService).logHospWaiting(DR_KRANSTEGE_PERSON_ID,
        DR_KRANSTEGE_HSA_ID);
  }

  @Test
  void shouldMonitorLogUserNotAuthorized() {
    final var kranstegeWaitingForHosp = kranstegeBuilder()
        .licensedHealthcareProfessions(
            List.of(
                new LicensedHealtcareProfession("SK", "Legitimerad sjuksköterska")
            )
        )
        .build();

    notifyPrivatePractitionerRegistration.notify(kranstegeWaitingForHosp);

    verify(monitoringLogService).logUserNotAuthorizedInHosp(DR_KRANSTEGE_PERSON_ID,
        DR_KRANSTEGE_HSA_ID);
  }

  @Test
  void shouldSendRegistrationStatusEmail() {
    notifyPrivatePractitionerRegistration.notify(kranstegeBuilder().build());

    verify(mailService).sendRegistrationStatusEmail(
        DR_KRANSTEGE.getRegistrationStatus(),
        DR_KRANSTEGE.getEmail()
    );
  }

  @Test
  void shouldMonitorLogUserRegistered() {
    notifyPrivatePractitionerRegistration.notify(kranstegeBuilder().build());

    verify(monitoringLogService).logUserRegistered(
        DR_KRANSTEGE_PERSON_ID,
        DR_KRANSTEGE_HSA_ID,
        DR_KRANSTEGE.getRegistrationStatus()
    );
  }
}