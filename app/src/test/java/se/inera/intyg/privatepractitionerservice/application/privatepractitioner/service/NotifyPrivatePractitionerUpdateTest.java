package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.mockito.Mockito.verify;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;

@ExtendWith(MockitoExtension.class)
class NotifyPrivatePractitionerUpdateTest {

  @Mock
  private MonitoringLogService monitoringLogService;
  @InjectMocks
  private NotifyPrivatePractitionerUpdate notifyPrivatePractitionerUpdate;

  @Test
  void shouldMonitorLogUserUpdated() {
    notifyPrivatePractitionerUpdate.notify(DR_KRANSTEGE);

    verify(monitoringLogService).logUserDetailsChanged(
        DR_KRANSTEGE_PERSON_ID,
        DR_KRANSTEGE_HSA_ID
    );
  }
}