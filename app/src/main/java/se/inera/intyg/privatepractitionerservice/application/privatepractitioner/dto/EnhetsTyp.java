package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EnhetsTyp {

  private HsaIdDTO enhetsId;
  private String enhetsnamn;
  private ArbetsplatsKodDTO arbetsplatskod;
  private String agarform;
  private String postadress;
  private String postnummer;
  private String postort;
  private String telefonnummer;
  private String epost;
  private LocalDateTime startdatum;
  private LocalDateTime slutdatum;
  private GeografiskIndelningDTO geografiskIndelning;
  private VerksamhetDTO verksamhetstyp;
  private CareProviderDTO vardgivare;
}
