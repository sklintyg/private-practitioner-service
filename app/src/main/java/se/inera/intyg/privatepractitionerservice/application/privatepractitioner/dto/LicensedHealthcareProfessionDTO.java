package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.LicensedHealthcareProfessionDTO.LicensedHealthcareProfessionDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = LicensedHealthcareProfessionDTOBuilder.class)
public class LicensedHealthcareProfessionDTO {

  String code;
  String name;

  @JsonPOJOBuilder(withPrefix = "")
  public static class LicensedHealthcareProfessionDTOBuilder {

  }
}