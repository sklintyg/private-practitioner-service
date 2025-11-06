package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import lombok.Data;

@Data
public class GetPrivatePractitionerResponseDTO {

  private HoSPersonDTO hoSPerson;
  private ResultCodeEnum resultCode;
  private String resultText;
}
