package se.inera.intyg.privatepractitionerservice.integration.api.hosp.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospCredentialsForPerson {

  private String personalIdentityNumber;
  @Builder.Default
  private List<HealthCareProfessionalLicence> healthCareProfessionalLicence = new ArrayList<>();
  private String personalPrescriptionCode;
  @Builder.Default
  private List<HCPSpecialityCodes> healthCareProfessionalLicenceSpeciality = new ArrayList<>();
  @Builder.Default
  private List<NursePrescriptionRight> nursePrescriptionRight = new ArrayList<>();
  private String healthcareProfessionalLicenseIdentityNumber;
  @Builder.Default
  private List<String> educationCode = new ArrayList<>();
  @Builder.Default
  private List<RestrictionDTO> restrictions = new ArrayList<>();
  private Boolean feignedPerson;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RestrictionDTO {

    private String healthCareProfessionalLicenceCode;
    private String restrictionCode;
    private String restrictionName;

    public boolean isValid() {
      return healthCareProfessionalLicenceCode != null
          && !healthCareProfessionalLicenceCode.isEmpty()
          && restrictionCode != null
          && !restrictionCode.isEmpty()
          && restrictionName != null
          && !restrictionName.isEmpty();
    }

    public RestrictionDTO validate() {
      if (!isValid()) {
        throw new IllegalStateException("RestrictionDTO is not valid");
      } else {
        return this;
      }
    }
  }
}
