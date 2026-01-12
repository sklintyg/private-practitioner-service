package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerRequest.ValidatePrivatePractitionerRequestBuilder;

@JsonDeserialize(builder = ValidatePrivatePractitionerRequestBuilder.class)
@Value
@Builder
public class ValidatePrivatePractitionerRequest {

  String personId;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ValidatePrivatePractitionerRequestBuilder {

  }
}
