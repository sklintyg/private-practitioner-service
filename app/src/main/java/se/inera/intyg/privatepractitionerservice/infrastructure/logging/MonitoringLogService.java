package se.inera.intyg.privatepractitionerservice.infrastructure.logging;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;

/**
 * Interface used when logging to monitoring file. Used to ensure that the log entries are uniform
 * and easy to parse.
 */
public interface MonitoringLogService {

  void logUserRegistered(String id, String hsaId, RegistrationStatus registrationStatus);

  void logUserErased(String id, String careProviderId);

  void logUserDetailsChanged(String id, String hsaId);

  void logHospWaiting(String id, String hsaId);

  void logUserAuthorizedInHosp(String id, String hsaId);

  void logUserNotAuthorizedInHosp(String id, String hsaId);

  void logRegistrationRemoved(String id, String hsaId);
}
