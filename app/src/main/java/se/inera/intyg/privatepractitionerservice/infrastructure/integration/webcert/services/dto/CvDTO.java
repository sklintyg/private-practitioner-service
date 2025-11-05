package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import lombok.Data;

@Data
public class CvDTO {

  private String code;
  private String codeSystem;
  private String codeSystemName;
  private String codeSystemVersion;
  private String displayName;
  private String originalText;
}
