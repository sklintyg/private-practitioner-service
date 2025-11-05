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
package se.inera.intyg.privatepractitionerservice.infrastructure.integration.hsa.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Adapter for converting XML Schema types to Java dates and vice versa.
 *
 * @author andreaskaltenbach
 */
public final class LocalDateAdapter {

  private static final ZoneId TIMEZONE = ZoneId.systemDefault();

  private static final String ISO_DATE_PATTERN = "yyyy-MM-dd";
  private static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  // CHECKSTYLE:OFF MagicNumber
  private static final DateTimeFormatter ISO_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern(ISO_DATE_TIME_PATTERN)
      .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
      .toFormatter();
  // CHECKSTYLE:ON MagicNumber

  private static final String XSD_DATE_TIMEZONE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}([+-].*|Z)";
  private static final String XSD_DATETIME_TIMEZONE_REGEXP = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.?[0-9]*([+-].*|Z)";

  private LocalDateAdapter() {
  }

  /**
   * Converts an xs:date to a java.time.LocalDate.
   */
  public static LocalDate parseDate(String dateString) {
    if (dateString.matches(XSD_DATE_TIMEZONE_REGEXP) || dateString.matches(
        XSD_DATETIME_TIMEZONE_REGEXP)) {
      return LocalDate.from(
          jakarta.xml.bind.DatatypeConverter.parseDate(dateString).toInstant().atZone(TIMEZONE));
    } else {
      return LocalDate.parse(dateString.substring(0, ISO_DATE_PATTERN.length()));
    }
  }

  /**
   * Converts an xs:datetime to a LocalDateTime.
   */
  public static LocalDateTime parseDateTime(String dateString) {
    if (dateString.matches(XSD_DATETIME_TIMEZONE_REGEXP) || dateString.matches(
        XSD_DATE_TIMEZONE_REGEXP)) {
      return LocalDateTime.from(
          jakarta.xml.bind.DatatypeConverter.parseDateTime(dateString).toInstant()
              .atZone(TIMEZONE));
    } else if (dateString.contains("T")) {
      return LocalDateTime.parse(dateString, ISO_DATETIME_FORMATTER);
    } else {
      return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(ISO_DATE_PATTERN))
          .atStartOfDay();
    }
  }

  /**
   * Print a DateTime.
   */
  public static String printDateTime(LocalDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN));
  }

  /**
   * Print a Date.
   */
  public static String printDate(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern(ISO_DATE_PATTERN));
  }
}
