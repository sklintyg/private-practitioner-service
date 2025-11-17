package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerServiceImpl implements UpdatePrivatePractitionerService {

  @Override
  public PrivatePractitionerDTO update(
      UpdatePrivatePractitionerRequest request) {
    return null;
  }
}
