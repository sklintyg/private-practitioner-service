package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class EraseServiceTest {

  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private HospRepository hospRepository;
  @Mock
  private MonitoringLogService monitoringLogService;
  @InjectMocks
  private EraseService eraseService;

  private static final String HSA_ID = DR_KRANSTEGE_HSA_ID;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", true);
  }

  @Test
  void shouldMonitorlogWhenPrivatePractitionerIsErased() {
    final var kranstege = kranstegeBuilder().build();
    doReturn(Optional.of(kranstege)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(true).when(hospRepository)
        .removeFromCertifier(kranstege, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(monitoringLogService).logUserErased(DR_KRANSTEGE_PERSON_ID, HSA_ID);
  }

  @Test
  void shouldEraseAccountWhenPrivatePractitionerIsFound() {
    final var kranstege = kranstegeBuilder().build();
    doReturn(Optional.of(kranstege)).when(privatePractitionerRepository)
        .findByHsaId(HSA_ID);
    doReturn(true).when(hospRepository)
        .removeFromCertifier(kranstege, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(privatePractitionerRepository).remove(kranstege);
  }

  @Test
  void shouldThrowExceptionWhenDeletePrivatePractitionerFailure() {
    final var kranstege = kranstegeBuilder().build();
    doReturn(true).when(hospRepository)
        .removeFromCertifier(kranstege, "Avslutat konto i Webcert.");
    doReturn(Optional.of(kranstege)).when(privatePractitionerRepository)
        .findByHsaId(HSA_ID);
    doThrow(new RuntimeException()).when(privatePractitionerRepository)
        .remove(kranstege);

    assertThrows(RuntimeException.class, () -> eraseService.erasePrivatePractitioner(HSA_ID));
  }

  @Test
  void shouldNotEraseAccountWhenFailureErasingCertifier() {
    final var kranstege = kranstegeBuilder().build();
    doReturn(Optional.of(kranstege)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(false).when(hospRepository)
        .removeFromCertifier(kranstege, "Avslutat konto i Webcert.");

    assertThrows(PrivatlakarportalServiceException.class,
        () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoMoreInteractions(privatePractitionerRepository);
  }

  @Test
  void shouldThrowIfExceptionWhenErasingCertifier() {
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doThrow(new IllegalStateException("Exception")).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");

    assertThrows(Exception.class, () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoMoreInteractions(privatePractitionerRepository);
  }

  @Test
  void shouldNotMonitorLogWhenResultNotOkFromEraseCertifier() {
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(false).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");

    assertThrows(PrivatlakarportalServiceException.class,
        () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoInteractions(monitoringLogService);
  }

  @Test
  void shouldNotEraseAccountWhenPrivatePractitionerNotFound() {
    doReturn(Optional.empty()).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(privatePractitionerRepository);
  }

  @Test
  void shouldNotEraseFromCertifierWhenPrivatePractitionerNotFound() {
    doReturn(Optional.empty()).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(hospRepository);
  }

  @Test
  void shouldNotMonitorLogWhenNoPrivatePractitionerFound() {
    doReturn(Optional.empty()).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoInteractions(monitoringLogService);
  }

  @Test
  void shouldNotEraseAccountWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(privatePractitionerRepository);
  }

  @Test
  void shouldNotEraseFromCertifierWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(hospRepository);
  }

  @Test
  void shouldNotMonitorLogWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoInteractions(monitoringLogService);
  }
}
