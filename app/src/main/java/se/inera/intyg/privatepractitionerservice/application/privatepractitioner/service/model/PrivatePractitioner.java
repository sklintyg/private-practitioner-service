/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

  public void updateWithHospInformation(HospPerson hosp) {
    if (!hosp.getPersonalIdentityNumber().equalsIgnoreCase(this.personId)) {
      throw new IllegalArgumentException("Personal identity number does not match!");
    }
    personalPrescriptionCode = hosp.getPersonalPrescriptionCode();
    specialties = hosp.getSpecialities().stream()
        .filter((Speciality::isPhysicianSpeciality))
        .toList();
    licensedHealthcareProfessions = hosp.getLicensedHealthcareProfessions();
    hospUpdated = hosp.getHospUpdated();
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
    if (isLicensedPhysician()) {
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
