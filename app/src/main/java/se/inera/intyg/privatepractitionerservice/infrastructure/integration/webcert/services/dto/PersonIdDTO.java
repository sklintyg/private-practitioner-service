package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import lombok.Data;

@Data
public class PersonIdDTO {

  private String root;
  private String extension;
  private String identifierName;
}
