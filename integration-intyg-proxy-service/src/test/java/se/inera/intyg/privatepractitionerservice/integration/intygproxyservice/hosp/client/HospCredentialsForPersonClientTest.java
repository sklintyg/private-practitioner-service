package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.TRACE_ID_KEY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonRequestDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonResponseDTO;

@ExtendWith(MockitoExtension.class)
class HospCredentialsForPersonClientTest {

  private static final GetCredentialsForPersonRequestDTO GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO =
      GetCredentialsForPersonRequestDTO.builder()
          .personId("personId")
          .build();

  @Mock
  private RestClient restClient;

  @InjectMocks
  private HospCredentialsForPersonClient credentialsForPersonClient;

  private ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    final var uri = "/api/from/configuration";
    ReflectionTestUtils.setField(credentialsForPersonClient, "credentialsForPersonEndpoint", uri);

    RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    responseSpec = mock(ResponseSpec.class);

    MDC.put(TRACE_ID_KEY, "traceId");
    MDC.put(SESSION_ID_KEY, "sessionId");

    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(GetCredentialsForPersonRequestDTO.class))).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_TRACE_ID_HEADER, "traceId")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_SESSION_ID_HEADER, "sessionId")).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void shallReturnGetCitizenCertificatesResponse() {
    final var expectedResponse = GetCredentialsForPersonResponseDTO.builder()
        .credentials(new HospCredentialsForPerson())
        .build();

    doReturn(expectedResponse).when(responseSpec).body(GetCredentialsForPersonResponseDTO.class);

    final var actualResponse = credentialsForPersonClient.get(
        GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO);

    assertEquals(expectedResponse, actualResponse);
  }
}
