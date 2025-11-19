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