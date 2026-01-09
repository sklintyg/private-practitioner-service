package se.inera.intyg.privatepractitionerservice.integration.api.hosp.model;

import lombok.Data;

@Data
public class NursePrescriptionRight {

  protected String healthCareProfessionalLicence;
  protected boolean prescriptionRight;
}
