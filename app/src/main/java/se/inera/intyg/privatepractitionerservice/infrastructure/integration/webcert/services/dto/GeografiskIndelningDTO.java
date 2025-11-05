package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import lombok.Data;

@Data
public class GeografiskIndelningDTO {

  private CvDTO lan;
  private CvDTO kommun;
}
