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
package se.inera.intyg.privatlakarportal.service;

import se.inera.intyg.privatlakarportal.common.model.Registration;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.service.model.HospInformation;
import se.inera.intyg.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.intyg.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatlakarportal.web.integration.test.dto.PrivatlakareDto;

/**
 * Created by pebe on 2015-06-25.
 */
public interface RegisterService {

  RegistrationStatus createRegistration(Registration registration, Long godkantMedgivandeVersion);

  SaveRegistrationResponseStatus saveRegistration(Registration registration);

  HospInformation getHospInformation();

  boolean removePrivatlakare(String personId);

  void injectHsaInterval(int hsaIdNotificationInterval);

  PrivatlakareDto getPrivatlakare(String personId);
}
