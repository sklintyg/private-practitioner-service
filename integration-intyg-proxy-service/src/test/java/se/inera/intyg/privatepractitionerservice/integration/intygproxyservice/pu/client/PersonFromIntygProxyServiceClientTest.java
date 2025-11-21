package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client;

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
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@ExtendWith(MockitoExtension.class)
class PersonFromIntygProxyServiceClientTest {

  @Mock
  private RestClient restClient;

  @InjectMocks
  private PersonFromIntygProxyServiceClient personFromIntygProxyServiceClient;

  private RequestBodyUriSpec requestBodyUriSpec;
  private ResponseSpec responseSpec;

  private static final String PERSON_ID = "197705232382";
  public static final String KRANSTEGE_FIRST_NAME = "Frida";
  public static final String KRANSTEGE_LAST_NAME = "Kranstege";

  @BeforeEach
  void setUp() {
    final var uri = "/api/from/person";
    ReflectionTestUtils.setField(personFromIntygProxyServiceClient, "getPuPath", uri);

    requestBodyUriSpec = mock(RequestBodyUriSpec.class);
    responseSpec = mock(ResponseSpec.class);

    MDC.put(TRACE_ID_KEY, "traceId");
    MDC.put(SESSION_ID_KEY, "sessionId");

    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(GetPersonIntegrationRequest.class))).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_TRACE_ID_HEADER, "traceId")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.header(LOG_SESSION_ID_HEADER, "sessionId")).thenReturn(
        requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
  }


  @Test
  void shouldReturnGetPersonResponse() {
    final var expectedResponse = getPersonSvarDTO();

    doReturn(expectedResponse).when(responseSpec).body(PersonSvarDTO.class);

    final var actualResponse = personFromIntygProxyServiceClient.get(
        GetPersonIntegrationRequest.builder().personId(PERSON_ID).build());

    assertEquals(expectedResponse, actualResponse);
  }

  private static PersonDTO getPersonDTO() {
    return new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, KRANSTEGE_LAST_NAME);
  }

  private static PersonSvarDTO getPersonSvarDTO() {
    return new PersonSvarDTO(getPersonDTO(), StatusDTO.FOUND);
  }
}