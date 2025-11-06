package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import lombok.Data;

@Data
public class HsaIdDTO {

  private String root;
  private String extension;
  private String identifierName;
}
