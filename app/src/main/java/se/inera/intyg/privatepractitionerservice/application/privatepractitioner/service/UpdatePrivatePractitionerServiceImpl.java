package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.UpdatePrivatePractitionerRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerServiceImpl implements UpdatePrivatePractitionerService {
  private final PrivatePractitionerRepository repository;
  private final UpdatePrivatePractitionerRequestValidator validator;

  @Override
  @Transactional
  public PrivatePractitionerDTO update(
      UpdatePrivatePractitionerRequest request) {
    validator.validate(request);
    return null;
  }
}
