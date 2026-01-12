package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.HospCertificationPersonClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonRequestDTO;

@Service
@RequiredArgsConstructor
public class GetHospCertificationPersonService {

  private final HospCertificationPersonClient hospCertificationPersonClient;

  public Result get(GetHospCertificationPersonRequestDTO hospCertificationPersonRequestDTO) {
    validateRequest(hospCertificationPersonRequestDTO);
    final var hospCertificationPersonResponseDTO = hospCertificationPersonClient.get(
        hospCertificationPersonRequestDTO);
    return hospCertificationPersonResponseDTO.getResult();
  }

  private void validateRequest(
      GetHospCertificationPersonRequestDTO hospCertificationPersonRequestDTO) {
    if (hospCertificationPersonRequestDTO.getPersonId() == null
        || hospCertificationPersonRequestDTO.getPersonId().isEmpty()) {
      throw new IllegalArgumentException("Missing required parameter 'personId'");
    }
    if (hospCertificationPersonRequestDTO.getOperation() == null
        || hospCertificationPersonRequestDTO.getOperation().isEmpty()) {
      throw new IllegalArgumentException("Missing required parameter 'operation'");
    }
  }
}
