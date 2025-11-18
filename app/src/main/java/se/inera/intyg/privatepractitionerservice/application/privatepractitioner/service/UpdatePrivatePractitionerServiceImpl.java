package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.UpdatePrivatePractitionerRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerServiceImpl implements UpdatePrivatePractitionerService {

  private final PrivatePractitionerRepository repository;
  private final UpdatePrivatePractitionerRequestValidator validator;
  private final HashUtility hashUtility;
  private final PrivatePractitionerConverter converter;
  private final NotifyPrivatePractitionerUpdate notifyPrivatePractitionerUpdate;

  @Override
  @Transactional
  public PrivatePractitionerDTO update(
      UpdatePrivatePractitionerRequest request) {

    validator.validate(request);

    final var existingPrivatePractitioner = getExistingPrivatePractitioner(request);
    final var updatedPrivatePractitioner = updateFields(existingPrivatePractitioner, request);

    final var savedPrivatePractitioner = repository.save(updatedPrivatePractitioner);

    notifyPrivatePractitionerUpdate.notify(savedPrivatePractitioner);

    return converter.convert(savedPrivatePractitioner);
  }


  private PrivatePractitioner getExistingPrivatePractitioner(
      UpdatePrivatePractitionerRequest request) {
    return repository.findByPersonId(request.getPersonId())
        .orElseThrow(() -> new PrivatlakarportalServiceException(
            PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
            "Private practitioner with personId %s not found.".formatted(
                hashUtility.hash(request.getPersonId()))));
  }

  private PrivatePractitioner updateFields(
      PrivatePractitioner existing,
      UpdatePrivatePractitionerRequest request) {
    existing.setPosition(request.getPosition());
    existing.setCareProviderName(request.getCareProviderName());
    existing.setCareUnitName(request.getCareUnitName());
    existing.setTypeOfCare(request.getTypeOfCare());
    existing.setHealthcareServiceType(request.getHealthcareServiceType());
    existing.setWorkplaceCode(request.getWorkplaceCode());
    existing.setPhoneNumber(request.getPhoneNumber());
    existing.setEmail(request.getEmail());
    existing.setAddress(request.getAddress());
    existing.setZipCode(request.getZipCode());
    existing.setCity(request.getCity());
    existing.setMunicipality(request.getMunicipality());
    existing.setCounty(request.getCounty());

    return existing;
  }
}
