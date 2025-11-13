package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.SpecialityDTO.SpecialityDTOBuilder;

@Value
@Builder
@JsonDeserialize(builder = SpecialityDTOBuilder.class)
public class SpecialityDTO {

  String code;
  String name;

  @JsonPOJOBuilder(withPrefix = "")
  public static class SpecialityDTOBuilder {

  }
}