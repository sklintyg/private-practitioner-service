package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;

@Component
@RequiredArgsConstructor
public class PrivatePractitionerValidationHelper {

  private final CodeSystemRepository codeSystemRepository;

  public void validatePersonId(String personId) {
    if (personId == null || personId.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "PersonId is required"
      );
    }
  }

  public void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Name is required"
      );
    }
  }

  public void validatePosition(String position) {
    if (position == null || position.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Position is required"
      );
    }

    if (!codeSystemRepository.positionExists(position)) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Position '%s' is invalid".formatted(position)
      );
    }
  }

  public void validateCareUnitName(String careUnitName) {
    if (careUnitName == null || careUnitName.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "CareUnitName is required"
      );
    }
  }

  public void validateTypeOfCare(String typeOfCare) {
    if (typeOfCare == null || typeOfCare.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "TypeOfCare is required"
      );
    }

    if (!codeSystemRepository.typeOfCareExists(typeOfCare)) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "TypeOfCare '%s' is invalid".formatted(typeOfCare)
      );
    }
  }

  public void validateHealthcareServiceType(String healthcareServiceType) {
    if (healthcareServiceType == null || healthcareServiceType.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "HealthcareServiceType is required"
      );
    }

    if (!codeSystemRepository.healthcareServiceTypeExists(healthcareServiceType)) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "HealthcareServiceType '%s' is invalid".formatted(healthcareServiceType)
      );
    }
  }

  public void validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "PhoneNumber is required"
      );
    }
  }

  public void validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Email is required"
      );
    }
  }

  public void validateAddress(String address) {
    if (address == null || address.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Address is required"
      );
    }
  }

  public void validateZipCode(String zipCode) {
    if (zipCode == null || zipCode.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "ZipCode is required"
      );
    }
  }

  public void validateCity(String city) {
    if (city == null || city.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "City is required"
      );
    }
  }

  public void validateMunicipality(String municipality) {
    if (municipality == null || municipality.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Municipality is required"
      );
    }
  }

  public void validateCounty(String county) {
    if (county == null || county.isBlank()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "County is required"
      );
    }
  }
}
