package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client;

import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;

@Service
public class GetPersonFromIntygProxyService {

  public PersonSvarDTO getPersonFromIntygProxy(GetPersonIntegrationRequest personRequest) {
    return PersonSvarDTO.builder().build();
  }
}
