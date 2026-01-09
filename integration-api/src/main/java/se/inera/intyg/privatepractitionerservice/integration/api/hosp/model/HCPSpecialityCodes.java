package se.inera.intyg.privatepractitionerservice.integration.api.hosp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HCPSpecialityCodes implements Serializable {

  private static final long serialVersionUID = 1L;
  protected String healthCareProfessionalLicenceCode;
  protected String specialityCode;
  protected String specialityName;

  public boolean isValid() {
    return healthCareProfessionalLicenceCode != null
        && !healthCareProfessionalLicenceCode.isEmpty()
        && specialityCode != null
        && !specialityCode.isEmpty()
        && specialityName != null
        && !specialityName.isEmpty();
  }

  public HCPSpecialityCodes validate() {
    if (!isValid()) {
      throw new IllegalStateException("HCPSpecialityCodes is not valid");
    } else {
      return this;
    }
  }
}
