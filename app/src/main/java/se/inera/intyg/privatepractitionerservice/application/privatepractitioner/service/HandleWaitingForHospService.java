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
      monitoringLogService.logHospWaiting(
          privatePractitioner.getPersonId(), privatePractitioner.getHsaId());
      mailService.sendRegistrationStatusEmail(
          privatePractitioner.getRegistrationStatus(), privatePractitioner.getEmail());
      privatePractitionerRepository.addEmailNotification(
          privatePractitioner.getPersonId(), LocalDateTime.now());
    }
  }

  private boolean isTimeToRemoveRegistration(PrivatePractitioner privatePractitioner) {
    return privatePractitioner.getEmailCount() >= numberOfEmails;
  }
}
