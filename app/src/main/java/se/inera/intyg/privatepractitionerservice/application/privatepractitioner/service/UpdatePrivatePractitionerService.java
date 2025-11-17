package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;

public interface UpdatePrivatePractitionerService {
  PrivatePractitionerDTO update(UpdatePrivatePractitionerRequest request);
}
