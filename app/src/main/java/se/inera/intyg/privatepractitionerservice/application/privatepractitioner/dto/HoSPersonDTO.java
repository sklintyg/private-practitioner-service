package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HoSPersonDTO {

  private HsaIdDTO hsaId;
  private PersonIdDTO personId;
  private String fullstandigtNamn;
  private List<BefattningDTO> befattning = new ArrayList<>();
  private List<SpecialitetDTO> specialitet = new ArrayList<>();
  private List<LegitimeradYrkesgruppDTO> legitimeradYrkesgrupp = new ArrayList<>();
  private String forskrivarkod;
  private boolean godkandAnvandare;
  private EnhetsTyp enhet;
}
