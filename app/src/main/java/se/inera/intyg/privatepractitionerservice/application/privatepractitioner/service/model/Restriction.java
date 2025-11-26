package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

public record Restriction(String code, String name,
                          String healthCareProfessionalLicenceCode) {


  public static final String REVOKED_LICENSE = "001";

  public boolean isRestrictedPhysician() {
    return code.equals(REVOKED_LICENSE) && healthCareProfessionalLicenceCode.equals("LK");
  }
}
