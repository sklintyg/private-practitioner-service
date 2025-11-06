package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CareProviderDTO {

  private HsaIdDTO vardgivareId;
  private String vardgivarenamn;
  private LocalDateTime startdatum;
  private LocalDateTime slutdatum;
}
