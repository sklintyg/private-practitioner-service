package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest.GetHospInformationRequestBuilder;

@JsonDeserialize(builder = GetHospInformationRequestBuilder.class)
@Value
@Builder
public class GetHospInformationRequest {

  String personId;
  
  @JsonPOJOBuilder(withPrefix = "")
  public static class GetHospInformationRequestBuilder {

  }
}
