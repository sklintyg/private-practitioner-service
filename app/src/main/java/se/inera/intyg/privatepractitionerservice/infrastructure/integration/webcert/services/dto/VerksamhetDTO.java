package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class VerksamhetDTO {

  private List<CvDTO> verksamhet = new ArrayList<>();
  private List<CvDTO> vardform = new ArrayList<>();
}
