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
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class UpdateHospService {

  private final HospRepository hospRepository;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  private final HandleWaitingForHospService handleWaitingForHospService;

  @Transactional
  public void update() {
    if (!hospRepository.needUpdateFromHosp()) {
      return;
    }

    final var privatePractitioners =
        privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate();
    privatePractitioners.forEach(
        privatePractitioner -> {
          final var hospPerson = hospRepository.findByPersonId(privatePractitioner.getPersonId());
          privatePractitioner.updateWithHospInformation(hospPerson);
          privatePractitionerRepository.save(privatePractitioner);

          switch (privatePractitioner.getRegistrationStatus()) {
            case AUTHORIZED -> notifyPrivatePractitionerRegistration.notify(privatePractitioner);
            case WAITING_FOR_HOSP, NOT_AUTHORIZED ->
                handleWaitingForHospService.handle(privatePractitioner);
          }
        });

    hospRepository.hospUpdated();
  }
}
