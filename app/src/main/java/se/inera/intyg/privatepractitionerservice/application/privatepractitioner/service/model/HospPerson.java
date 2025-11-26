package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospPerson {

  private String personalIdentityNumber;
  private String personalPrescriptionCode;
  @Builder.Default
  private List<LicensedHealtcareProfession> licensedHealthcareProfessions = new ArrayList<>();
  @Builder.Default
  private List<Restriction> restrictions = new ArrayList<>();
  @Builder.Default
  private List<Speciality> specialities = new ArrayList<>();
  @Builder.Default
  private List<String> specialityNames = new ArrayList<>();
  @Builder.Default
  private List<String> specialityCodes = new ArrayList<>();
  @Builder.Default
  private List<String> hsaTitles = new ArrayList<>();
  @Builder.Default
  private List<String> titleCodes = new ArrayList<>();
  @With
  private LocalDateTime hospUpdated;
}
