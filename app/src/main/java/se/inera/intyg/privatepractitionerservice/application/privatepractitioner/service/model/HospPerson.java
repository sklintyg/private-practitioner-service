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
  private List<LicensedHealtcareProfession> licensedHealthcareProfessions;
  private List<Speciality> specialities;
  private List<String> specialityNames = new ArrayList<>();
  private List<String> specialityCodes = new ArrayList<>();
  private List<String> hsaTitles = new ArrayList<>();
  private List<String> titleCodes = new ArrayList<>();
  @With
  private LocalDateTime hospUpdated;
}
