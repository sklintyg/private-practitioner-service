/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.privatepractitionerservice.infrastructure.integration.hsa.services;

import se.inera.intyg.privatepractitionerservice.infrastructure.integration.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatepractitionerservice.infrastructure.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Privatlakare;

/**
 * Created by pebe on 2015-09-03.
 */
public interface HospUpdateService {

  void scheduledUpdateHospInformation();

  void updateHospInformation();

  RegistrationStatus updateHospInformation(Privatlakare privatlakare,
      boolean shouldRegisterInCertifier)
      throws HospUpdateFailedToContactHsaException;

  void checkForUpdatedHospInformation(Privatlakare privatlakare);

  /**
   * Only used for testing purposes. Do *NOT* use outside TestController.
   */
  void resetTimer();
}
