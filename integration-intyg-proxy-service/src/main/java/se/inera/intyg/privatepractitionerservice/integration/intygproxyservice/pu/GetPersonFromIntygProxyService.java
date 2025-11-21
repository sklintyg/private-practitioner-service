package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.PersonFromIntygProxyServiceClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;

@Service
@RequiredArgsConstructor
public class GetPersonFromIntygProxyService {

  private final PersonFromIntygProxyServiceClient personFromIntygProxyServiceClient;

  public PersonSvarDTO getPersonFromIntygProxy(GetPersonIntegrationRequest personRequest) {
    return personFromIntygProxyServiceClient.get(personRequest);
  }
}
