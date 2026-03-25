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
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatePrivatePractitionerFromPUService {

  private final GetPersonIntegrationService getPersonIntegrationService;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HashUtility hashUtility;

  public PrivatePractitioner updateFromPu(PrivatePractitioner practitioner) {
    try {
      final var request = new GetPersonIntegrationRequest(practitioner.getPersonId());

      final var response = getPersonIntegrationService.getPerson(request);
      if (response.getStatus() != Status.FOUND) {
        log.warn(
            "Could not update private practitioner from PU for personId {}. Reason: Person not found in PU.",
            hashUtility.hash(practitioner.getPersonId()));
        return practitioner;
      }

      final var puPerson = response.getPerson();
      final var hasNoChanges = practitioner.getName().equals(puPerson.getName());

      if (hasNoChanges) {
        return practitioner;
      }

      practitioner.setName(puPerson.getName());

      return privatePractitionerRepository.save(practitioner);
    } catch (Exception e) {
      log.info(
          "Could not update private practitioner from PU for personId '{}'. Reason: {}",
          hashUtility.hash(practitioner.getPersonId()),
          e.getMessage());
      return practitioner;
    }
  }
}
