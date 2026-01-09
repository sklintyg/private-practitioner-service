package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonRequestDTO;

@ExtendWith(MockitoExtension.class)
class IntygProxyServiceHospServiceTest {

  private static final String PERSON_ID = "personId";
  private static final String CERTIFICATION_ID = "certificationId";
  public static final String OPERATION = "operation";
  public static final String REASON = "reason";

  @Mock
  GetHospLastUpdateService hospLastUpdateService;

  @Mock
  private GetHospCredentialsForPersonService getHospCredentialsForPersonService;

  @Mock
  private GetHospCertificationPersonService hospCertificationPersonService;

  @InjectMocks
  private IntygProxyServiceHospService intygProxyServiceHospService;

  @Nested
  class HandleHospCertificationPersonResponse {

    @Test
    void shouldReturnResultForHospCertificationPerson() {
      final var expectedResponse = new Result();

      final var request = GetHospCertificationPersonRequestDTO.builder()
          .personId(PERSON_ID)
          .certificationId(CERTIFICATION_ID)
          .reason(REASON)
          .operation(OPERATION)
          .build();

      when(hospCertificationPersonService.get(request)).thenReturn(expectedResponse);

      final var result = intygProxyServiceHospService.handleHospCertificationPersonResponseType(
          CERTIFICATION_ID,
          OPERATION, PERSON_ID, REASON);

      assertEquals(expectedResponse, result);
    }
  }

  @Nested
  class GetHospLastUpdate {

    @Test
    void shouldReturnHospLastUpdate() {
      final var expectedResponse = LocalDateTime.now();

      when(hospLastUpdateService.get()).thenReturn(expectedResponse);

      final var result = intygProxyServiceHospService.getHospLastUpdate();

      assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowWebServiceExceptionIfIllegalStateExceptionIsCaught() {
      when(hospLastUpdateService.get()).thenThrow(IllegalStateException.class);

      assertThrows(IllegalStateException.class,
          () -> intygProxyServiceHospService.getHospLastUpdate());
    }
  }

  @Nested
  class GetHospCredentialsForPersonResponseType {

    @Test
    void shouldReturnCredentialsForPerson() {
      final var expectedResult = new HospCredentialsForPerson();

      when(getHospCredentialsForPersonService.get(PERSON_ID)).thenReturn(expectedResult);

      final var result = intygProxyServiceHospService.getHospCredentialsForPersonResponseType(
          PERSON_ID);

      assertEquals(expectedResult, result);
    }

    @Test
    void shouldThrowWebServiceExceptionIfHospCredentialsForPersonIsNull() {
      when(getHospCredentialsForPersonService.get(PERSON_ID)).thenReturn(null);

      assertThrows(IllegalStateException.class,
          () -> intygProxyServiceHospService.getHospCredentialsForPersonResponseType(
              PERSON_ID));
    }
  }
}
