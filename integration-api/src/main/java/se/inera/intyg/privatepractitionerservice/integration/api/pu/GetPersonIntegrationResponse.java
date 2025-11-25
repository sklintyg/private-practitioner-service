package se.inera.intyg.privatepractitionerservice.integration.api.pu;

import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;

@Value
@Builder
public class GetPersonIntegrationResponse {

  Person person;
  Status status;
}
