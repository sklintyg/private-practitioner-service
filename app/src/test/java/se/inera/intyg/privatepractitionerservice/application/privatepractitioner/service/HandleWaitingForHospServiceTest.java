package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus.AUTHORIZED;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class HandleWaitingForHospServiceTest {

  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private MailService mailService;
  @Mock
  private MonitoringLogService monitoringLogService;
  @Mock
  private RemovePrivatePractitionerService removePrivatePractitionerService;
  @InjectMocks
  private HandleWaitingForHospService service;

  @BeforeEach
  void init() {
    try {
      var field = HandleWaitingForHospService.class.getDeclaredField("numberOfEmails");
      field.setAccessible(true);
      field.set(service, 3);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldRemovePrivatePractitionerWhenEmailCountIsEnough() {
    PrivatePractitioner practitioner = mock(PrivatePractitioner.class);
    when(practitioner.getEmailCount()).thenReturn(3);

    service.handle(practitioner);

    verify(removePrivatePractitionerService).remove(practitioner);
    verifyNoMoreInteractions(mailService, monitoringLogService, privatePractitionerRepository);
  }

  @Test
  void shouldSendNotificationAndLogWhenEmailCountIsNotEnough() {
    service.handle(DR_KRANSTEGE);

    verify(monitoringLogService).logHospWaiting(DR_KRANSTEGE_PERSON_ID, DR_KRANSTEGE_HSA_ID);
    verify(mailService).sendRegistrationStatusEmail(AUTHORIZED, DR_KRANSTEGE_EMAIL);
    verify(privatePractitionerRepository).addEmailNotification(eq(DR_KRANSTEGE_PERSON_ID),
        any(LocalDateTime.class));
    verifyNoInteractions(removePrivatePractitionerService);
  }
}