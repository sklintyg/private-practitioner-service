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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospInformation;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Registration;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.testability.dto.PrivatlakareDto;

/**
 * Created by pebe on 2015-06-25.
 */
public interface RegisterService {

  RegistrationStatus createRegistration(String personalIdentityNumber, Registration registration,
      Long godkantMedgivandeVersion);

  SaveRegistrationResponseStatus saveRegistration(String personalIdentityNumber,
      Registration registration);

  HospInformation getHospInformation(String personalIdentityNumber);

  boolean removePrivatlakare(String personId);

  void injectHsaInterval(int hsaIdNotificationInterval);

  PrivatlakareDto getPrivatlakare(String personId);
}
