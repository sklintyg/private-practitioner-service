package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.HospLastUpdateClient;

@Service
@RequiredArgsConstructor
public class GetHospLastUpdateService {

  private final HospLastUpdateClient hospLastUpdateClient;

  public LocalDateTime get() {
    final var hospLastUpdateResponseDTO = hospLastUpdateClient.get();
    return hospLastUpdateResponseDTO.getLastUpdate();
  }
}
