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
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonRequestDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonResponseDTO;

@ExtendWith(MockitoExtension.class)
class HospCertificationPersonClientTest {

  private static final String PERSON_ID = "personId";
  private static final String CERTIFICATION_ID = "certificationId";
  private static final Result RESULT = new Result();
  public static final String OPERATION = "operation";
  public static final String REASON = "reason";

  @Mock
  private RestClient restClient;

  @InjectMocks
  private HospCertificationPersonClient hospCertificationPersonClient;

  private RequestBodyUriSpec requestBodyUriSpec;
  private ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    final var uri = "/api/from/configuration";
    ReflectionTestUtils.setField(hospCertificationPersonClient, "certificationPersonEndpoint", uri);

    requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    responseSpec = mock(ResponseSpec.class);

    MDC.put(TRACE_ID_KEY, "traceId");
    MDC.put(SESSION_ID_KEY, "sessionId");

    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(GetHospCertificationPersonRequestDTO.class))).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_TRACE_ID_HEADER, "traceId")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_SESSION_ID_HEADER, "sessionId")).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void shallReturnGetCitizenCertificatesResponse() {
    final var request = GetHospCertificationPersonRequestDTO.builder()
        .personId(PERSON_ID)
        .certificationId(CERTIFICATION_ID)
        .operation(OPERATION)
        .reason(REASON)
        .build();

    final var expectedResponse = GetHospCertificationPersonResponseDTO.builder()
        .result(RESULT)
        .build();

    doReturn(expectedResponse).when(responseSpec).body(GetHospCertificationPersonResponseDTO.class);

    final var actualResponse = hospCertificationPersonClient.get(request);

    assertEquals(expectedResponse, actualResponse);
  }
}
