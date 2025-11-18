package se.inera.intyg.privatepractitionerservice.testability.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest.TestabilityCreateRegistrationRequestBuilder;

@JsonDeserialize(builder = TestabilityCreateRegistrationRequestBuilder.class)
@Value
@Builder
public class TestabilityCreateRegistrationRequest {

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
  public static class TestabilityCreateRegistrationRequestBuilder {

  }
}