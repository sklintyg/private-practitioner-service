package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest.CreateRegistrationRequestBuilder;

@JsonDeserialize(builder = CreateRegistrationRequestBuilder.class)
@Value
@Builder
public class CreateRegistrationRequest {

  String personId;
  String name;

  String position;
  String careUnitName;
  String ownershipType;
  String typeOfCare;
  String healthcareServiceType;
  String workplaceCode;

  String phoneNumber;
  String email;
  String address;
  String zipCode;
  String city;
  String municipality;
  String county;

  @JsonPOJOBuilder(withPrefix = "")
  public static class CreateRegistrationRequestBuilder {

  }
}
