package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

  private String personnummer;
  private String namn;
  private String fornamn;
  private String mellannamn;
  private String efternamn;
}
