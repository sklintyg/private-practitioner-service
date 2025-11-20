package se.inera.intyg.privatepractitionerservice.integration.api.pu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPersonIntegrationRequest {

  private String personId;
}
