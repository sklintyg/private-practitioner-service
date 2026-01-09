package se.inera.intyg.privatepractitionerservice.integration.api.hosp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result {

  private String resultCode;
  private String resultText;
}
