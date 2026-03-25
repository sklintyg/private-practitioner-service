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
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdatePrivatePractitionerService {

  private final PrivatePractitionerRepository repository;
  private final UpdatePrivatePractitionerRequestValidator validator;
  private final HashUtility hashUtility;
  private final PrivatePractitionerConverter converter;
  private final MonitoringLogService monitoringLogService;

  @Transactional
  public PrivatePractitionerDTO update(UpdatePrivatePractitionerRequest request) {

    validator.validate(request);

    final var existingPrivatePractitioner = getExistingPrivatePractitioner(request);
    final var updatedPrivatePractitioner = updateFields(existingPrivatePractitioner, request);

    final var savedPrivatePractitioner = repository.save(updatedPrivatePractitioner);

    monitoringLogService.logUserDetailsChanged(
        savedPrivatePractitioner.getPersonId(), savedPrivatePractitioner.getHsaId());

    return converter.convert(savedPrivatePractitioner);
  }

  private PrivatePractitioner getExistingPrivatePractitioner(
      UpdatePrivatePractitionerRequest request) {
    return repository
        .findByPersonId(request.getPersonId())
        .orElseThrow(
            () ->
                new PrivatlakarportalServiceException(
                    PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
                    "Private practitioner with personId %s not found."
                        .formatted(hashUtility.hash(request.getPersonId()))));
  }

  private PrivatePractitioner updateFields(
      PrivatePractitioner existing, UpdatePrivatePractitionerRequest request) {
    existing.setPosition(request.getPosition());
    existing.setCareProviderName(request.getCareUnitName());
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
