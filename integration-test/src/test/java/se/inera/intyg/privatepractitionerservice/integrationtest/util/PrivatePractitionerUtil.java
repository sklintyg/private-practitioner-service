package se.inera.intyg.privatepractitionerservice.integrationtest.util;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;

public class PrivatePractitionerUtil {

  public static String privatePractitionerPersonId(PrivatePractitionerDTO response) {
    if (response == null) {
      return null;
    }
    return response.getPersonId();
  }
}
