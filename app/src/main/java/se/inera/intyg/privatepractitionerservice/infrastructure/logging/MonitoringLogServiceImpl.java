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
package se.inera.intyg.privatepractitionerservice.infrastructure.logging;

import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.EVENT_ACTION;
import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.EVENT_PRIVATE_PRACTITIONER_ID;
import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.ORGANIZATION_ID;
import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.USER_ID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringLogServiceImpl implements MonitoringLogService {

  private final HashUtility hashUtility;
  private static final Object SPACE = " ";

  private static final Marker MONITORING = MarkerFactory.getMarker("Monitoring");

  @Override
  public void logUserRegistered(String id, String hsaId, RegistrationStatus registrationStatus) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_REGISTERED))
            .put(USER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_REGISTERED, hashedPersonId, hsaId,
          registrationStatus);
    }
  }

  @Override
  public void logUserErased(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_DELETED))
            .put(EVENT_PRIVATE_PRACTITIONER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_DELETED, hsaId);
    }
  }

  @Override
  public void logUserDetailsChanged(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_DETAILS_CHANGED))
            .put(USER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_DETAILS_CHANGED, hashedPersonId);
    }
  }

  @Override
  public void logHospWaiting(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.HOSP_WAITING))
            .put(EVENT_PRIVATE_PRACTITIONER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.HOSP_WAITING, hashedPersonId);
    }
  }

  @Override
  public void logUserAuthorizedInHosp(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.HOSP_AUTHORIZED))
            .put(EVENT_PRIVATE_PRACTITIONER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.HOSP_AUTHORIZED, hashedPersonId);
    }
  }

  @Override
  public void logUserNotAuthorizedInHosp(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.HOSP_NOT_AUTHORIZED))
            .put(EVENT_PRIVATE_PRACTITIONER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.HOSP_NOT_AUTHORIZED, hashedPersonId);
    }
  }

  @Override
  public void logRegistrationRemoved(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.REGISTRATION_REMOVED))
            .put(EVENT_PRIVATE_PRACTITIONER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.REGISTRATION_REMOVED, hashedPersonId);
    }
  }

  private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
    log.info(MONITORING, buildMessage(logEvent), logMsgArgs);
  }

  private String buildMessage(MonitoringEvent logEvent) {
    StringBuilder logMsg = new StringBuilder();
    logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
    return logMsg.toString();
  }

  @Getter
  private enum MonitoringEvent {
    USER_REGISTERED("User '{}' registered with hsaId '{}', returned status '{}'"),
    USER_DELETED("User '{}' deleted"),
    USER_DETAILS_CHANGED("Details for user '{}' changed"),
    HOSP_WAITING("User '{}' is waiting for HOSP"),
    HOSP_AUTHORIZED("User '{}' is authorized doctor in HOSP"),
    HOSP_NOT_AUTHORIZED("User '{}' is not authorized doctor in HOSP"),
    REGISTRATION_REMOVED("User '{}' exceeded number of registration attempts, removing user");

    private final String message;

    MonitoringEvent(String msg) {
      this.message = msg;
    }
  }

  private String toEventType(MonitoringEvent monitoringEvent) {
    return monitoringEvent.name().toLowerCase().replace("_", "-");
  }
}
