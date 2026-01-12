package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client;

import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.TRACE_ID_KEY;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonRequestDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonResponseDTO;

@Service
@RequiredArgsConstructor
public class HospCredentialsForPersonClient {

  @Qualifier("intygProxyServiceRestClient")
  private final RestClient ipsRestClient;

  @Value("${integration.intygproxyservice.credentialsforperson.endpoint}")
  private String credentialsForPersonEndpoint;

  public GetCredentialsForPersonResponseDTO get(
      GetCredentialsForPersonRequestDTO getCredentialsForPersonRequestDTO) {
    return ipsRestClient
        .post()
        .uri(credentialsForPersonEndpoint)
        .body(getCredentialsForPersonRequestDTO)
        .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
        .header(LOG_SESSION_ID_HEADER,
            MDC.get(SESSION_ID_KEY) != null ? MDC.get(SESSION_ID_KEY) : "-"
        )
        .contentType(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GetCredentialsForPersonResponseDTO.class);
  }
}
