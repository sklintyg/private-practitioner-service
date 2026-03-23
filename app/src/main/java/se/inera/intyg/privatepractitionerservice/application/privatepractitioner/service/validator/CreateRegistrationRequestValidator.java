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
          PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS, "Registration already exists");
    }
  }
}
