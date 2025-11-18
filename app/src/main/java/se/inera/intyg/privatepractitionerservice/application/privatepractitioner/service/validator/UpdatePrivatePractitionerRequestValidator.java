package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;

@Component
@RequiredArgsConstructor
public class UpdatePrivatePractitionerRequestValidator {

  private final PrivatePractitionerValidationHelper validationHelper;

  public void validate(UpdatePrivatePractitionerRequest update) {
    validationHelper.validatePersonId(update.getPersonId());
    validationHelper.validateName(update.getName());
    validationHelper.validatePosition(update.getPosition());
    validationHelper.validateCareUnitName(update.getCareUnitName());
    validationHelper.validateTypeOfCare(update.getTypeOfCare());
    validationHelper.validateHealthcareServiceType(update.getHealthcareServiceType());
    validationHelper.validatePhoneNumber(update.getPhoneNumber());
    validationHelper.validateEmail(update.getEmail());
    validationHelper.validateAddress(update.getAddress());
    validationHelper.validateZipCode(update.getZipCode());
    validationHelper.validateCity(update.getCity());
    validationHelper.validateMunicipality(update.getMunicipality());
    validationHelper.validateCounty(update.getCounty());
  }
}
