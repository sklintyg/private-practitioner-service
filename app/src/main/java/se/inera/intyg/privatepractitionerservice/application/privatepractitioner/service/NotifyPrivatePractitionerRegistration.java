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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyPrivatePractitionerRegistration {

  private final MonitoringLogService monitoringLogService;
  private final MailService mailService;

  public void notify(PrivatePractitioner privatePractitioner) {
    final var registrationStatus = privatePractitioner.getRegistrationStatus();
    switch (registrationStatus) {
      case AUTHORIZED ->
          monitoringLogService.logUserAuthorizedInHosp(
              privatePractitioner.getPersonId(), privatePractitioner.getHsaId());
      case WAITING_FOR_HOSP ->
          monitoringLogService.logHospWaiting(
              privatePractitioner.getPersonId(), privatePractitioner.getHsaId());
      case NOT_AUTHORIZED ->
          monitoringLogService.logUserNotAuthorizedInHosp(
              privatePractitioner.getPersonId(), privatePractitioner.getHsaId());
      default ->
          log.info(
              "Registration status {} does not trigger any monitoring log.", registrationStatus);
    }

    mailService.sendRegistrationStatusEmail(registrationStatus, privatePractitioner.getEmail());

    monitoringLogService.logUserRegistered(
        privatePractitioner.getPersonId(), privatePractitioner.getHsaId(), registrationStatus);
  }
}
