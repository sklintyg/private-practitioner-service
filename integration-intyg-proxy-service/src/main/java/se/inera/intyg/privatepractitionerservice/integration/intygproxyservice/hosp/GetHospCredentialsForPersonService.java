package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.HospCredentialsForPersonClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonRequestDTO;

@Service
@RequiredArgsConstructor
public class GetHospCredentialsForPersonService {

  private final HospCredentialsForPersonClient credentialsForPersonClient;

  public HospCredentialsForPerson get(String personId) {
    validateRequest(personId);
    final var credentialsForPersonResponseDTO = credentialsForPersonClient.get(
        GetCredentialsForPersonRequestDTO.builder()
            .personId(personId)
            .build()
    );
    return credentialsForPersonResponseDTO.getCredentials();
  }

  private void validateRequest(String personId) {
    if (personId == null || personId.isEmpty()) {
      throw new IllegalArgumentException("Missing required parameter 'personId'");
    }
  }
}
