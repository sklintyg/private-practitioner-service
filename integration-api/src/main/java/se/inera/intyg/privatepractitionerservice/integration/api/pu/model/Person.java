package se.inera.intyg.privatepractitionerservice.integration.api.pu.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Person {

  String personId;
  String name;
}
