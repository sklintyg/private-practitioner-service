package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateRegistrationRequestValidator {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final PrivatePractitionerValidationHelper validationHelper;

  public void validate(CreateRegistrationRequest registration) {
    validationHelper.validatePersonId(registration.getPersonId());
    validationHelper.validateName(registration.getName());
    validationHelper.validatePosition(registration.getPosition());
    validationHelper.validateCareUnitName(registration.getCareUnitName());
    validationHelper.validateTypeOfCare(registration.getTypeOfCare());
    validationHelper.validateHealthcareServiceType(registration.getHealthcareServiceType());
    validationHelper.validatePhoneNumber(registration.getPhoneNumber());
    validationHelper.validateEmail(registration.getEmail());
    validationHelper.validateAddress(registration.getAddress());
    validationHelper.validateZipCode(registration.getZipCode());
    validationHelper.validateCity(registration.getCity());
    validationHelper.validateMunicipality(registration.getMunicipality());
    validationHelper.validateCounty(registration.getCounty());

    if (privatePractitionerRepository.isExists(registration.getPersonId())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS,
          "Registration already exists"
      );
    }
  }
}
