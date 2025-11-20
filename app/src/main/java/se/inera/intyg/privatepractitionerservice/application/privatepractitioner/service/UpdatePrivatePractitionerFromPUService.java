package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatePrivatePractitionerFromPUService {

  private final GetPersonIntegrationService getPersonIntegrationService;
  private final PrivatePractitionerRepository privatePractitionerRepository;

  public PrivatePractitioner updateFromPu(PrivatePractitioner practitioner) {
    final var request = GetPersonIntegrationRequest.builder()
        .personId(practitioner.getPersonId())
        .build();

    final var response = getPersonIntegrationService.getPerson(request);

    if (response.getStatus() != Status.FOUND) {
      return practitioner;
    }

    final var puPerson = response.getPerson();
    final var hasNoChanges = practitioner.getName().equals(puPerson.getName());

    if (hasNoChanges) {
      return practitioner;
    }

    practitioner.setName(puPerson.getName());

    return privatePractitionerRepository.save(practitioner);
  }

}

