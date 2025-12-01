package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailNotifications {

  @Builder.Default
  List<LocalDateTime> notificationDates = List.of();

  public boolean isTimeToNotify(Duration durationBetweenEmails, int numberOfEmails) {
    if (notificationDates.size() < numberOfEmails) {
      if (notificationDates.isEmpty()) {
        return true;
      }
      final var lastNotification = notificationDates.stream().sorted().toList().getLast();
      return lastNotification.plus(durationBetweenEmails).isBefore(LocalDateTime.now());
    }
    return false;
  }
}
