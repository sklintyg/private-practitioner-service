package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.TRACE_ID_KEY;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospLastUpdateResponseDTO;

@ExtendWith(MockitoExtension.class)
class HospLastUpdateClientTest {

  @Mock
  private RestClient restClient;

  @InjectMocks
  private HospLastUpdateClient hospLastUpdateClient;

  private RequestHeadersUriSpec requestBodyUriSpec;
  private ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    final var uri = "/api/from/configuration";
    ReflectionTestUtils.setField(hospLastUpdateClient, "lastUpdateEndpoint", uri);

    requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
    responseSpec = mock(ResponseSpec.class);

    MDC.put(TRACE_ID_KEY, "traceId");
    MDC.put(SESSION_ID_KEY, "sessionId");

    when(restClient.get()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_TRACE_ID_HEADER, "traceId")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_SESSION_ID_HEADER, "sessionId")).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void shallReturnGetCitizenCertificatesResponse() {
    final var expectedResponse = GetHospLastUpdateResponseDTO.builder()
        .lastUpdate(LocalDateTime.now())
        .build();

    doReturn(expectedResponse).when(responseSpec).body(GetHospLastUpdateResponseDTO.class);

    final var actualResponse = hospLastUpdateClient.get();

    assertEquals(expectedResponse, actualResponse);
  }
}
