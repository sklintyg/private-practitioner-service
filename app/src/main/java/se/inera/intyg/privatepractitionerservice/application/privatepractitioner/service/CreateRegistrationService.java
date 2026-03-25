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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerFactory;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.CreateRegistrationRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateRegistrationService {

  private final CreateRegistrationRequestValidator createRegistrationRequestValidator;
  private final PrivatePractitionerFactory privatePractitionerFactory;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HospRepository hospRepository;
  private final NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  private final PrivatePractitionerConverter privatePractitionerConverter;

  @Transactional
  public PrivatePractitionerDTO createRegistration(CreateRegistrationRequest registration) {
    createRegistrationRequestValidator.validate(registration);

    final var privatePractitioner = privatePractitionerFactory.create(registration);

    final var hospPerson = hospRepository.findByPersonId(privatePractitioner.getPersonId());

    privatePractitioner.updateWithHospInformation(hospPerson);

    final var savedPrivatePractitioner = privatePractitionerRepository.save(privatePractitioner);

    hospRepository.addToCertifier(savedPrivatePractitioner);

    notifyPrivatePractitionerRegistration.notify(savedPrivatePractitioner);

    return privatePractitionerConverter.convert(savedPrivatePractitioner);
  }
}
