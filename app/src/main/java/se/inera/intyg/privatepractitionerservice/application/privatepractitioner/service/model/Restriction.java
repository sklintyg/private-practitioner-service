package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.HospConstants.RESTRICTION_CODE_REVOKED;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.HospConstants;

public record Restriction(String code, String name,
                          String healthCareProfessionalLicenceCode) {

  public boolean isRestrictedPhysician() {
    return code.equals(RESTRICTION_CODE_REVOKED) && healthCareProfessionalLicenceCode.equals(
        HospConstants.LK);
  }
}
