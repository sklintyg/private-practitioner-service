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
package se.inera.intyg.privatlakarportal.hsa.monitoring;


import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.EVENT_ACTION;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.EVENT_PRIVATE_PRACTITIONER_ID;
import static se.inera.intyg.privatlakarportal.logging.MdcLogConstants.ORGANIZATION_ID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.monitoring.logging.LogMarkers;
import se.inera.intyg.privatlakarportal.logging.HashUtility;
import se.inera.intyg.privatlakarportal.logging.MdcCloseableMap;


@Slf4j
@Service("hsaMonitoringLogService")
@RequiredArgsConstructor
public class MonitoringLogServiceImpl implements MonitoringLogService {

  private final HashUtility hashUtility;

  private static final Object SPACE = " ";

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
    log.info(LogMarkers.MONITORING, buildMessage(logEvent), logMsgArgs);
  }

  private String buildMessage(MonitoringEvent logEvent) {
    StringBuilder logMsg = new StringBuilder();
    logMsg.append(logEvent.name()).append(SPACE).append(logEvent.getMessage());
    return logMsg.toString();
  }

  private enum MonitoringEvent {
    HOSP_WAITING("User '{}' is waiting for HOSP"),
    HOSP_AUTHORIZED("User '{}' is authorized doctor in HOSP"),
    HOSP_NOT_AUTHORIZED("User '{}' is not authorized doctor in HOSP"),
    REGISTRATION_REMOVED("User '{}' exceeded number of registration attempts, removing user");

    private String message;

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
