package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto;

import lombok.Data;

@Data
public class GetPrivatePractitionerResponseDTO {

  private HoSPersonDTO hoSPerson;
  private ResultCodeEnum resultCode;
  private String resultText;
}
