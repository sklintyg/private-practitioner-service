package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.HospLastUpdateClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospLastUpdateResponseDTO;

@ExtendWith(MockitoExtension.class)
class GetHospLastUpdateServiceTest {

  @Mock
  private HospLastUpdateClient hospLastUpdateClient;

  @InjectMocks
  private GetHospLastUpdateService getHospLastUpdateService;

  @Test
  void shouldReturnHospLastUpdate() {
    final var expectedResponse = GetHospLastUpdateResponseDTO.builder()
        .lastUpdate(LocalDateTime.now())
        .build();

    when(hospLastUpdateClient.get()).thenReturn(expectedResponse);

    final var result = getHospLastUpdateService.get();

    assertEquals(expectedResponse.getLastUpdate(), result);
  }
}
