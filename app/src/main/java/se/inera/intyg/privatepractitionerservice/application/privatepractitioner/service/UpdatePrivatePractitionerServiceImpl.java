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
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerServiceImpl implements UpdatePrivatePractitionerService {

  private final PrivatePractitionerRepository repository;
  private final UpdatePrivatePractitionerRequestValidator validator;
  private final HashUtility hashUtility;
  private final HospRepository hospRepository;
  private final PrivatePractitionerConverter converter;

  @Override
  @Transactional
  public PrivatePractitionerDTO update(
      UpdatePrivatePractitionerRequest request) {

    validator.validate(request);

    final var existingPrivatePractitioner = getExistingPrivatePractitioner(request);
    final var updatedPrivatePractitioner = updateFields(existingPrivatePractitioner, request);

    final var hospPerson = hospRepository.findByPersonId(existingPrivatePractitioner.getPersonId());
    hospPerson.ifPresent(updatedPrivatePractitioner::updateWithHospInformation);

    final var savedPrivatePractitioner = repository.save(updatedPrivatePractitioner);

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
    return PrivatePractitioner.builder()
        .hsaId(existing.getHsaId())
        .registrationDate(existing.getRegistrationDate())
        .startDate(existing.getStartDate())
        .endDate(existing.getEndDate())
        .hospUpdated(existing.getHospUpdated())
        .personId(request.getPersonId())
        .name(request.getName())
        .position(request.getPosition())
        .careProviderName(request.getCareUnitName())
        .careUnitName(request.getCareUnitName())
        .ownershipType("Privat")
        .typeOfCare(request.getTypeOfCare())
        .healthcareServiceType(request.getHealthcareServiceType())
        .workplaceCode(request.getWorkplaceCode())
        .phoneNumber(request.getPhoneNumber())
        .email(request.getEmail())
        .address(request.getAddress())
        .zipCode(request.getZipCode())
        .city(request.getCity())
        .municipality(request.getMunicipality())
        .county(request.getCounty())
        .build();
  }
}
