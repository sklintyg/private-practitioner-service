package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client;

import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.config.IntygProxyServiceRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.config.IntygProxyServiceRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.config.IntygProxyServiceRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.config.IntygProxyServiceRestClientConfig.TRACE_ID_KEY;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;

@Service
public class PersonFromIntygProxyServiceClient {


  @Qualifier("hsaIntygProxyServiceRestClient")
  private RestClient ipsRestClient;

  @Autowired
  public PersonFromIntygProxyServiceClient(RestClient ipsRestClient) {
    this.ipsRestClient = ipsRestClient;
  }

  @Value("${integration.intygproxyservice.person.endpoint}")
  private String personUppgifterEndpoint;


  public PersonSvarDTO get(GetPersonIntegrationRequest getPersonIntegrationRequest) {
    return ipsRestClient
        .post()
        .uri(personUppgifterEndpoint)
        .body(getPersonIntegrationRequest)
        .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
        .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
        .contentType(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(PersonSvarDTO.class);
  }
}
