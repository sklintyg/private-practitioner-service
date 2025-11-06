package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

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
