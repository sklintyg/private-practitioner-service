package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import lombok.Data;

@Data
public class HsaIdDTO {

  private String root;
  private String extension;
  private String identifierName;
}
