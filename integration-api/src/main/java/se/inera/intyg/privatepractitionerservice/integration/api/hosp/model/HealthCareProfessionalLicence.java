package se.inera.intyg.privatepractitionerservice.integration.api.hosp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthCareProfessionalLicence {

  protected String healthCareProfessionalLicenceCode;
  protected String healthCareProfessionalLicenceName;

  public boolean isValid() {
    return healthCareProfessionalLicenceCode != null
        && !healthCareProfessionalLicenceCode.isEmpty()
        && healthCareProfessionalLicenceName != null
        && !healthCareProfessionalLicenceName.isEmpty();
  }

  public HealthCareProfessionalLicence validate() {
    if (!isValid()) {
      throw new IllegalStateException("HealthCareProfessionalLicence is not valid");
    } else {
      return this;
    }
  }
}
