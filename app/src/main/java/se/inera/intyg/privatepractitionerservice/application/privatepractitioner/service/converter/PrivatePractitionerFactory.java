package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;

@Component
public class PrivatePractitionerFactory {

  public PrivatePractitioner create(CreateRegistrationRequest createRegistrationRequest) {
    return PrivatePractitioner.builder()
        .personId(createRegistrationRequest.getPersonId())
        .name(createRegistrationRequest.getName())
        .position(createRegistrationRequest.getPosition())
        .careProviderName(createRegistrationRequest.getCareUnitName())
//        .ownershipType(createRegistrationRequest.getOwnershipType())
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
        .consentFormVersion(createRegistrationRequest.getConsentFormVersion())
        .ownershipType("Privat")
        .registrationDate(LocalDateTime.now())
        .build();
  }
}
