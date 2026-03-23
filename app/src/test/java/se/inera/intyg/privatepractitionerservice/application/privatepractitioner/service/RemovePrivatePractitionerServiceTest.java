/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class RemovePrivatePractitionerServiceTest {

  @Mock private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock private HospRepository hospRepository;
  @Mock private MailService mailService;
  @Mock private MonitoringLogService monitoringLogService;
  @InjectMocks private RemovePrivatePractitionerService service;

  @Test
  void shouldRemovePractitionerAndSendEmailAndLogWhenHospRemoveSucceeds() {
    when(hospRepository.removeFromCertifier(DR_KRANSTEGE, "remove")).thenReturn(true);

    service.remove(DR_KRANSTEGE);

    verify(privatePractitionerRepository).remove(DR_KRANSTEGE);
    verify(mailService).sendRegistrationRemovedEmail(DR_KRANSTEGE);
    verify(monitoringLogService)
        .logRegistrationRemoved(DR_KRANSTEGE_PERSON_ID, DR_KRANSTEGE_HSA_ID);
  }

  @Test
  void shouldNotRemovePractitionerOrSendEmailOrLogWhenHospRemoveFails() {
    when(hospRepository.removeFromCertifier(DR_KRANSTEGE, "remove")).thenReturn(false);

    service.remove(DR_KRANSTEGE);

    verifyNoInteractions(privatePractitionerRepository, mailService, monitoringLogService);
  }
}
