package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client;

import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.config.IntygProxyServiceRestClientConfig.TRACE_ID_KEY;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospLastUpdateResponseDTO;

@Service
public class HospLastUpdateClient {

  @Autowired
  @Qualifier("intygProxyServiceRestClient")
  private RestClient ipsRestClient;

  @Value("${integration.intygproxyservice.lastupdate.endpoint}")
  private String lastUpdateEndpoint;

  public GetHospLastUpdateResponseDTO get() {
    return ipsRestClient
        .get()
        .uri(lastUpdateEndpoint)
        .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
        .header(LOG_SESSION_ID_HEADER,
            MDC.get(SESSION_ID_KEY) != null ? MDC.get(SESSION_ID_KEY) : "-"
        )
        .retrieve()
        .body(GetHospLastUpdateResponseDTO.class);
  }
}
