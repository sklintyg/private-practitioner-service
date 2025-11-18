package se.inera.intyg.privatepractitionerservice.testability.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityResetPrivatePractitionerRequest.TestabilityResetPrivatePractitionerRequestBuilder;

@JsonDeserialize(builder = TestabilityResetPrivatePractitionerRequestBuilder.class)
@Value
@Builder
public class TestabilityResetPrivatePractitionerRequest {

  @Builder.Default
  List<String> personIds = Collections.emptyList();

  @JsonPOJOBuilder(withPrefix = "")
  public static class TestabilityResetPrivatePractitionerRequestBuilder {

  }
}
