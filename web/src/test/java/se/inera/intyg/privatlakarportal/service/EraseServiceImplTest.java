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
package se.inera.intyg.privatlakarportal.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.hsa.services.HospPersonService;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.monitoring.MonitoringLogService;

@ExtendWith(MockitoExtension.class)
class EraseServiceImplTest {

  @Mock
  private PrivatlakareRepository privatlakareRepository;
  @Mock
  private MonitoringLogService monitoringLogService;
  @Mock
  private HospPersonService hospPersonService;

  @InjectMocks
  private EraseServiceImpl eraseService;

  private static final String HSA_ID = "SE23100000175-5TEST";
  private static final String CERTIFIER_ID_NULL = null;
  private static final String PERSON_ID = "1912121211212";
  private Privatlakare privatePractitioner;

  @BeforeEach
  void init() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", true);
    privatePractitioner = getPrivatePractitioner();
  }

  @Test
  void shouldMonitorlogWhenPrivatePractitionerIsErased() {
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doReturn(true).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(monitoringLogService).logUserErased(PERSON_ID, HSA_ID);
  }

  @Test
  void shouldEraseAccountWhenPrivatePractitionerIsFound() {
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doReturn(true).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL, "Avslutat konto i Webcert.");

    eraseService.erasePrivatePractitioner(HSA_ID);

    verify(privatlakareRepository).delete(privatePractitioner);
  }

  @Test
  void shouldThrowExceptionWhenDeletePrivatePractitionerFailure() {
    doReturn(true).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL, "Avslutat konto i Webcert.");
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doThrow(new RuntimeException()).when(privatlakareRepository).delete(privatePractitioner);

    assertThrows(RuntimeException.class, () -> eraseService.erasePrivatePractitioner(HSA_ID));
  }

  @Test
  void shouldNotEraseAccountWhenFailureErasingCertifier() {
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doReturn(false).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL, "Avslutat konto i Webcert.");

    assertThrows(PrivatlakarportalServiceException.class,
        () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoMoreInteractions(privatlakareRepository);
  }

  @Test
  void shouldThrowIfExceptionWhenErasingCertifier() {
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doThrow(new IllegalStateException("Exception")).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL,
            "Avslutat konto i Webcert.");

    assertThrows(Exception.class, () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoMoreInteractions(privatlakareRepository);
  }

  @Test
  void shouldNotMonitorLogWhenResultNotOkFromEraseCertifier() {
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);
    doReturn(false).when(hospPersonService)
        .removeFromCertifier(PERSON_ID, CERTIFIER_ID_NULL, "Avslutat konto i Webcert.");

    assertThrows(PrivatlakarportalServiceException.class,
        () -> eraseService.erasePrivatePractitioner(HSA_ID));

    verifyNoInteractions(monitoringLogService);
  }

  @Test
  void shouldNotEraseAccountWhenPrivatePractitionerNotFound() {
    doReturn(null).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(privatlakareRepository);
  }

  @Test
  void shouldNotEraseFromCertifierWhenPrivatePractitionerNotFound() {
    doReturn(null).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  void shouldNotMonitorLogWhenNoPrivatePractitionerFound() {
    doReturn(null).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoInteractions(monitoringLogService);
  }

  @Test
  void shouldNotEraseAccountWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(privatlakareRepository);
  }

  @Test
  void shouldNotEraseFromCertifierWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  void shouldNotMonitorLogWhenEraseConfigInactivated() {
    ReflectionTestUtils.setField(eraseService, "erasePrivatePractitioner", false);
    doReturn(privatePractitioner).when(privatlakareRepository).findByHsaId(HSA_ID);

    eraseService.erasePrivatePractitioner(HSA_ID);

    verifyNoInteractions(monitoringLogService);
  }

  private Privatlakare getPrivatePractitioner() {
    final var privatePractitioner = new Privatlakare();
    privatePractitioner.setHsaId(HSA_ID);
    privatePractitioner.setPersonId(PERSON_ID);
    return privatePractitioner;
  }
}
