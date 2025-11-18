package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.UpdatePrivatePractitionerRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerServiceImpl implements UpdatePrivatePractitionerService {

  private final PrivatePractitionerRepository repository;
  private final UpdatePrivatePractitionerRequestValidator validator;
  private final HashUtility hashUtility;

  @Override
  @Transactional
  public PrivatePractitionerDTO update(
      UpdatePrivatePractitionerRequest request) {
    validator.validate(request);

    if (!repository.isExists(request.getPersonId())) {
      throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Private practitioner with personId %s not found.".formatted(
              hashUtility.hash(request.getPersonId())));
    }
    return PrivatePractitionerDTO.builder().build();
  }
}
