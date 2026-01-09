package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse.ValidatePrivatePractitionerResponseBuilder;

@JsonDeserialize(builder = ValidatePrivatePractitionerResponseBuilder.class)
@Value
@Builder
public class ValidatePrivatePractitionerResponse {

  ValidatePrivatePractitionerResultCode resultCode;
  String resultText;

  @JsonPOJOBuilder(withPrefix = "")
  public static class ValidatePrivatePractitionerResponseBuilder {

  }
}
