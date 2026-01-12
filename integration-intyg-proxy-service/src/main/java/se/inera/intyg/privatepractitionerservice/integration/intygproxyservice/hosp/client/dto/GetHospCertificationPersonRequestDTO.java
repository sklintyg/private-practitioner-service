package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetHospCertificationPersonRequestDTO {

  String personId;
  String certificationId;
  String operation;
  String reason;
}
