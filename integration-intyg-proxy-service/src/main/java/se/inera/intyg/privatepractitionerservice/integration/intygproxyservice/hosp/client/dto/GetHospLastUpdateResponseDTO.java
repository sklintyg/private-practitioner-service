package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetHospLastUpdateResponseDTO {

  LocalDateTime lastUpdate;
}
