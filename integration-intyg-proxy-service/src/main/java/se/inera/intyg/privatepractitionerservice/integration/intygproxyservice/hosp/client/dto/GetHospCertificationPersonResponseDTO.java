package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetHospCertificationPersonResponseDTO {

  Result result;
}
