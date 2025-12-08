package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse.GetHospInformationResponseBuilder;

@JsonDeserialize(builder = GetHospInformationResponseBuilder.class)
@Value
@Builder
public class GetHospInformationResponse {

  String personId;
  String personalPrescriptionCode;
  @Builder.Default
  List<CodeDTO> licensedHealthcareProfessions = new ArrayList<>();
  @Builder.Default
  List<CodeDTO> specialities = new ArrayList<>();

  @JsonPOJOBuilder(withPrefix = "")
  public static class GetHospInformationResponseBuilder {

  }
}
