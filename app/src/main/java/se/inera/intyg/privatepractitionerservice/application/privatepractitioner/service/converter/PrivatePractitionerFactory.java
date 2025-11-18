package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;

@Component
public class PrivatePractitionerFactory {

  public PrivatePractitioner create(CreateRegistrationRequest createRegistrationRequest) {
    return PrivatePractitioner.builder()
        .personId(createRegistrationRequest.getPersonId())
        .name(createRegistrationRequest.getName())
        .position(createRegistrationRequest.getPosition())
        .careProviderName(createRegistrationRequest.getCareUnitName())
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
        .build();
  }

  public PrivatePractitioner create(
      UpdatePrivatePractitionerRequest updatePrivatePractitionerRequest) {
    return PrivatePractitioner.builder()
        .personId(updatePrivatePractitionerRequest.getPersonId())
        .name(updatePrivatePractitionerRequest.getName())
        .position(updatePrivatePractitionerRequest.getPosition())
        .careProviderName(updatePrivatePractitionerRequest.getCareUnitName())
        .ownershipType("Privat")
        .typeOfCare(updatePrivatePractitionerRequest.getTypeOfCare())
        .healthcareServiceType(updatePrivatePractitionerRequest.getHealthcareServiceType())
        .workplaceCode(updatePrivatePractitionerRequest.getWorkplaceCode())
        .phoneNumber(updatePrivatePractitionerRequest.getPhoneNumber())
        .email(updatePrivatePractitionerRequest.getEmail())
        .address(updatePrivatePractitionerRequest.getAddress())
        .zipCode(updatePrivatePractitionerRequest.getZipCode())
        .city(updatePrivatePractitionerRequest.getCity())
        .municipality(updatePrivatePractitionerRequest.getMunicipality())
        .county(updatePrivatePractitionerRequest.getCounty())
        .build();
  }
}
