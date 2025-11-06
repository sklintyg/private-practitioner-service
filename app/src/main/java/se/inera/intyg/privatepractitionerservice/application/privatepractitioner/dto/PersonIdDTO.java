package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import lombok.Data;

@Data
public class PersonIdDTO {

  private String root;
  private String extension;
  private String identifierName;
}
