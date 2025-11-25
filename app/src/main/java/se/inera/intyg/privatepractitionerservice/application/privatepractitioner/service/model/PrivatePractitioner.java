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

  String hsaId;
  String personId;
  @Setter
  String name;
  @Setter
  String careProviderName;

  @Setter
  String position;
  @Setter
  String careUnitName;
  String ownershipType;
  @Setter
  String typeOfCare;
  @Setter
  String healthcareServiceType;
  @Setter
  String workplaceCode;

  @Setter
  String phoneNumber;
  @Setter
  String email;
  @Setter
  String address;
  @Setter
  String zipCode;
  @Setter
  String city;
  @Setter
  String municipality;
  @Setter
  String county;

  String personalPrescriptionCode;
  @Builder.Default
  List<Speciality> specialties = List.of();
  @Builder.Default
  List<LicensedHealtcareProfession> licensedHealthcareProfessions = List.of();

  LocalDateTime startDate;
  LocalDateTime endDate;

  LocalDateTime registrationDate;
  LocalDateTime hospUpdated;

  public void updateWithHospInformation(HospPerson hosp) {
    if (!hosp.getPersonalIdentityNumber().equalsIgnoreCase(this.personId)) {
      throw new IllegalArgumentException("Personal identity number does not match!");
    }
    personalPrescriptionCode = hosp.getPersonalPrescriptionCode();
    specialties = hosp.getSpecialities() == null ? List.of()
        : hosp.getSpecialities().stream().toList();
    licensedHealthcareProfessions = hosp.getLicensedHealthcareProfessions() == null ? List.of()
        : hosp.getLicensedHealthcareProfessions().stream().toList();
    hospUpdated = hosp.getHospUpdated();
  }

  public void clearHospInformation() {
    personalPrescriptionCode = null;
    specialties = List.of();
    licensedHealthcareProfessions = List.of();
  }

  public boolean isLicensedPhysician() {
    return licensedHealthcareProfessions.stream()
        .anyMatch(LicensedHealtcareProfession::isLicensedPhysician);
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
