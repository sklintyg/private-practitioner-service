package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonSvarDTO {

  private PersonDTO person;
  private StatusDTO status;
}
