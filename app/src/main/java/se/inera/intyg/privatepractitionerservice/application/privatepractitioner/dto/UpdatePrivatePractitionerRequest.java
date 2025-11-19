package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = UpdatePrivatePractitionerRequest.UpdatePrivatePractitionerRequestBuilder.class)
public class UpdatePrivatePractitionerRequest {

  String personId;

  String position;
  String careUnitName;
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
  public static class UpdatePrivatePractitionerRequestBuilder {

  }
}
