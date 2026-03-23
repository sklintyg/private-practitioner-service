/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
