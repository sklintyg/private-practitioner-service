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
package se.inera.intyg.privatlakarportal.service.monitoring;


import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.EVENT_ACTION;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.EVENT_AUTHENTICATION_SCHEME;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.EVENT_PRIVATE_PRACTITIONER_ID;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.ORGANIZATION_ID;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.USER_ID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.logging.LogMarkers;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.logging.HashUtility;
import se.inera.intyg.privatlakarportal.logging.MdcCloseableMap;

@Slf4j
@Service("webMonitoringLogService")
@RequiredArgsConstructor
public class MonitoringLogServiceImpl implements MonitoringLogService {

  private final HashUtility hashUtility;
  private static final Object SPACE = " ";

  @Override
  public void logUserRegistered(String id, Long consentVersion, String hsaId,
      RegistrationStatus registrationStatus) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_REGISTERED))
            .put(USER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_REGISTERED, hashedPersonId, consentVersion, hsaId,
          registrationStatus);
    }
  }

  @Override
  public void logUserDeleted(String id, String hsaId) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_DELETED))
            .put(USER_ID, hashedPersonId)
            .put(ORGANIZATION_ID, hsaId)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_DELETED, hashedPersonId);
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
  public void logUserLogin(String id, String authenticationScheme) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_LOGIN))
            .put(USER_ID, hashedPersonId)
            .put(EVENT_AUTHENTICATION_SCHEME, authenticationScheme)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_LOGIN, hashedPersonId, authenticationScheme);
    }
  }

  @Override
  public void logUserLogout(String id, String authenticationScheme) {
    final var hashedPersonId = hashUtility.hash(id);
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(EVENT_ACTION, toEventType(MonitoringEvent.USER_LOGOUT))
            .put(USER_ID, hashedPersonId)
            .put(EVENT_AUTHENTICATION_SCHEME, authenticationScheme)
            .build()
    ) {
      logEvent(MonitoringEvent.USER_LOGOUT, hashedPersonId, authenticationScheme);
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

  private void logEvent(MonitoringEvent logEvent, Object... logMsgArgs) {
    log.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
  }

  private String buildMessage(MonitoringEvent logEvent) {
    StringBuilder logMsg = new StringBuilder();
    logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
    return logMsg.toString();
  }

  private enum MonitoringEvent {
    USER_REGISTERED(
        "User '{}' registered with consent version '{}' and hsaId '{}', returned status '{}'"),
    USER_DELETED("User '{}' deleted"),
    USER_LOGIN("Login user '{}' using scheme '{}'"),
    USER_LOGOUT("Logout user '{}' using scheme '{}'"),
    USER_DETAILS_CHANGED("Details for user '{}' changed");

    private final String message;

    MonitoringEvent(String msg) {
      this.message = msg;
    }

    public String getMessage() {
      return message;
    }
  }

  private String toEventType(MonitoringEvent monitoringEvent) {
    return monitoringEvent.name().toLowerCase().replace("_", "-");
  }
}
