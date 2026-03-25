/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
package se.inera.intyg.privatepractitionerservice.testability.service.factory;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;

@Component
public class TestabilityPrivatePractitionerFactory {

  public PrivatePractitioner create(
      TestabilityCreateRegistrationRequest createRegistrationRequest) {
    return PrivatePractitioner.builder()
        .personId(createRegistrationRequest.getPersonId())
        .name(createRegistrationRequest.getName())
        .position(createRegistrationRequest.getPosition())
        .careProviderName(createRegistrationRequest.getCareUnitName())
        .careUnitName(createRegistrationRequest.getCareUnitName())
        .ownershipType("Privat")
        .typeOfCare(createRegistrationRequest.getTypeOfCare())
        .healthcareServiceType(createRegistrationRequest.getHealthcareServiceType())
        .workplaceCode(createRegistrationRequest.getWorkplaceCode())
        .phoneNumber(createRegistrationRequest.getPhoneNumber())
        .email(createRegistrationRequest.getEmail())
        .address(createRegistrationRequest.getAddress())
        .zipCode(createRegistrationRequest.getZipCode())
        .city(createRegistrationRequest.getCity())
        .municipality(createRegistrationRequest.getMunicipality())
        .county(createRegistrationRequest.getCounty())
        .registrationDate(LocalDateTime.now())
        .startDate(LocalDateTime.now())
        .build();
  }
}
