/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(true).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(monitoringLogService).logUserErased(DR_KRANSTEGE_PERSON_ID, HSA_ID);
  }

  @Test
  void shouldEraseAccountWhenPrivatePractitionerIsFound() {
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(true).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(privatePractitionerRepository).remove(DR_KRANSTEGE);
  }

  @Test
  void shouldThrowExceptionWhenDeletePrivatePractitionerFailure() {
    doReturn(true).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doThrow(new RuntimeException()).when(privatePractitionerRepository).remove(DR_KRANSTEGE);

    assertThrows(RuntimeException.class, () -> eraseService.erasePrivatePractitioner(HSA_ID));
  }

  @Test
  void shouldNotEraseAccountWhenFailureErasingCertifier() {
    doReturn(Optional.of(DR_KRANSTEGE)).when(privatePractitionerRepository).findByHsaId(HSA_ID);
    doReturn(false).when(hospRepository)
        .removeFromCertifier(DR_KRANSTEGE, "Avslutat konto i Webcert.");

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
