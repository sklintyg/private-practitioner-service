package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

public record Restriction(String restrictionCode, String restrictionName,
                          String healthCareProfessionalLicenceCode) {


  public static final String REVOKED_LICENSE = "001";

  public boolean isRestrictedPhysician() {
    return healthCareProfessionalLicenceCode.equals("LK") && restrictionCode.equals(
        REVOKED_LICENSE);
  }
}
