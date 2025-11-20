package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationResponse;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.GetPersonFromIntygProxyService;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter.PersonSvarConverter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonIntegrationService implements GetPersonIntegrationService {

  private final GetPersonFromIntygProxyService getPersonFromIntygProxyService;
  private final PersonSvarConverter personSvarConverter;

  @Override
  public GetPersonIntegrationResponse getPerson(GetPersonIntegrationRequest personRequest) {
    final var personSvarDTO = getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest);

    return null;
  }
}
