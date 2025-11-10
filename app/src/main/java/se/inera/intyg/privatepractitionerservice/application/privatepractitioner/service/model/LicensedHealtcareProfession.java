package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

public record LicensedHealtcareProfession(String code, String name) {

  public boolean isLicensedPhysician() {
    return "Läkare".equals(name);
  }
}
