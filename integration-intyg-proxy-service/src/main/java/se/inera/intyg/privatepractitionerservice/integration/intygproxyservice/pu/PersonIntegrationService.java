package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationResponse;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter.PersonSvarConverter;

@Service
@RequiredArgsConstructor
public class PersonIntegrationService implements GetPersonIntegrationService {

  private final GetPersonFromIntygProxyService getPersonFromIntygProxyService;
  private final PersonSvarConverter personSvarConverter;

  @Override
  public GetPersonIntegrationResponse getPerson(GetPersonIntegrationRequest personRequest) {
    validateRequest(personRequest);

    final var personSvarDTO = getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest);
    validateResponse(personSvarDTO);
    return GetPersonIntegrationResponse.builder()
        .person(personSvarConverter.convertPerson(personSvarDTO.person()))
        .status(personSvarConverter.convertStatus(personSvarDTO.status()))
        .build();
  }

  private static void validateResponse(PersonSvarDTO personSvarDTO) {
    if (personSvarDTO.status() == StatusDTO.NOT_FOUND) {
      throw new IllegalStateException("Person not found in PU");
    }
    if (personSvarDTO.status() == StatusDTO.ERROR) {
      throw new IllegalStateException("Error occurred when retrieving person from PU");
    }
  }

  private void validateRequest(GetPersonIntegrationRequest personRequest) {
    if (personRequest == null || personRequest.getPersonId() == null || personRequest.getPersonId()
        .isEmpty()) {
      throw new IllegalArgumentException("Valid personRequest was not provided: " + personRequest);
    }
  }
}
