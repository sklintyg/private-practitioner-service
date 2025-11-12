package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.RegistrationConfigurationResponse.RegistrationConfigurationResponseBuilder;

@JsonDeserialize(builder = RegistrationConfigurationResponseBuilder.class)
@Value
@Builder
public class RegistrationConfigurationResponse {

  List<CodeDTO> positionCodes;
  List<CodeDTO> typeOfCareCodes;
  List<CodeDTO> healthcareServiceTypeCodes;

  @JsonPOJOBuilder(withPrefix = "")
  public static class RegistrationConfigurationResponseBuilder {

  }
}
