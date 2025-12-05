package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.HospConstants;

public record Speciality(String code, String name, String healthCareProfessionalLicenceCode) {

  public boolean isPhysicianSpeciality() {
    return healthCareProfessionalLicenceCode.equals(HospConstants.LK);
  }
}
