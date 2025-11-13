package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateRegistrationRequestValidator {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final CodeSystemRepository codeSystemRepository;

  public void validate(CreateRegistrationRequest registration) {
    if (registration.getPersonId() == null || registration.getPersonId().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "PersonId is required"
      );
    }

    if (registration.getName() == null || registration.getName().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Name is required"
      );
    }

    if (registration.getPosition() == null || registration.getPosition().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Position is required"
      );
    }

    if (!codeSystemRepository.positionExists(registration.getPosition())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Position '%s' is invalid".formatted(registration.getPosition())
      );
    }

    if (registration.getCareUnitName() == null || registration.getCareUnitName().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "CareUnitName is required"
      );
    }

    if (registration.getTypeOfCare() == null || registration.getTypeOfCare().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "TypeOfCare is required"
      );
    }

    if (!codeSystemRepository.typeOfCareExists(registration.getTypeOfCare())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "TypeOfCare '%s' is invalid".formatted(registration.getTypeOfCare())
      );
    }

    if (registration.getHealthcareServiceType() == null || registration.getHealthcareServiceType()
        .isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "HealthcareServiceType is required"
      );
    }

    if (!codeSystemRepository.healthcareServiceTypeExists(
        registration.getHealthcareServiceType())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "HealthcareServiceType '%s' is invalid".formatted(registration.getHealthcareServiceType())
      );
    }

    if (registration.getPhoneNumber() == null || registration.getPhoneNumber().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "PhoneNumber is required"
      );
    }

    if (registration.getEmail() == null || registration.getEmail().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Email is required"
      );
    }

    if (registration.getAddress() == null || registration.getAddress().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Address is required"
      );
    }

    if (registration.getZipCode() == null || registration.getZipCode().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "ZipCode is required"
      );
    }

    if (registration.getCity() == null || registration.getCity().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "City is required"
      );
    }

    if (registration.getMunicipality() == null || registration.getMunicipality().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Municipality is required"
      );
    }

    if (registration.getCounty() == null || registration.getCounty().isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "County is required"
      );
    }

    if (privatePractitionerRepository.isExists(registration.getPersonId())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS,
          "Registration already exists"
      );
    }
  }
}
