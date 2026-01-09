package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PrivatePractitioner {

  private String hsaId;
  private String personId;
  @Setter
  private String name;
  @Setter
  private String careProviderName;

  @Setter
  private String position;
  @Setter
  private String careUnitName;
  private String ownershipType;
  @Setter
  private String typeOfCare;
  @Setter
  private String healthcareServiceType;
  @Setter
  private String workplaceCode;

  @Setter
  private String phoneNumber;
  @Setter
  private String email;
  @Setter
  private String address;
  @Setter
  private String zipCode;
  @Setter
  private String city;
  @Setter
  private String municipality;
  @Setter
  private String county;

  private String personalPrescriptionCode;
  @Builder.Default
  private List<Speciality> specialties = List.of();
  @Builder.Default
  private List<Restriction> restrictions = List.of();
  @Builder.Default
  private List<LicensedHealtcareProfession> licensedHealthcareProfessions = List.of();

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private LocalDateTime registrationDate;
  private LocalDateTime hospUpdated;

  private int emailCount;

  public void updateWithHospInformation(HospPerson hosp) {
    if (!hosp.getPersonalIdentityNumber().equalsIgnoreCase(this.personId)) {
      throw new IllegalArgumentException("Personal identity number does not match!");
    }

    hospUpdated = hosp.getHospUpdated();
    personalPrescriptionCode = hosp.getPersonalPrescriptionCode();
    specialties = hosp.getSpecialities().stream()
        .filter((Speciality::isPhysicianSpeciality))
        .toList();
    licensedHealthcareProfessions = hosp.getLicensedHealthcareProfessions();
    restrictions = hosp.getRestrictions().stream()
        .filter(Restriction::isRestrictedPhysician)
        .toList();
  }

  public boolean isLicensedPhysician() {
    return licensedHealthcareProfessions.stream()
        .anyMatch(LicensedHealtcareProfession::isLicensedPhysician);
  }

  public boolean isRestrictedPhysician() {
    return restrictions.stream().anyMatch(Restriction::isRestrictedPhysician);
  }

  public RegistrationStatus getRegistrationStatus() {
    if (licensedHealthcareProfessions.isEmpty()) {
      return RegistrationStatus.WAITING_FOR_HOSP;
    }
    if (isLicensedPhysician() && !isRestrictedPhysician()) {
      return RegistrationStatus.AUTHORIZED;
    }
    return RegistrationStatus.NOT_AUTHORIZED;
  }

  public boolean needHospUpdate(LocalDateTime hospLastUpdate) {
    return hospUpdated == null || hospLastUpdate == null || hospUpdated.isBefore(hospLastUpdate);
  }

  public boolean isFirstLogin() {
    return startDate == null;
  }

  public void firstLogin() {
    startDate = LocalDateTime.now();
  }
}
