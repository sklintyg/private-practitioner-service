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
package se.inera.intyg.privatlakarportal.web.controller.api.dto;

import se.inera.intyg.privatlakarportal.common.model.Registration;
import se.inera.intyg.privatlakarportal.service.model.HospInformation;

/**
 * Created by pebe on 2015-08-06.
 */

public class GetRegistrationResponse {

  private Registration registration;

  private HospInformation hospInformation;

  private boolean webcertUserTermsApproved;

  public GetRegistrationResponse(Registration registration, HospInformation hospInformation,
      boolean webcertUserTermsApproved) {
    this.registration = registration;
    this.hospInformation = hospInformation;
    this.webcertUserTermsApproved = webcertUserTermsApproved;
  }

  public Registration getRegistration() {
    return registration;
  }

  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  public HospInformation getHospInformation() {
    return hospInformation;
  }

  public void setHospInformation(HospInformation hospInformation) {
    this.hospInformation = hospInformation;
  }

  public boolean isWebcertUserTermsApproved() {
    return webcertUserTermsApproved;
  }

  public void setWebcertUserTermsApproved(boolean webcertUserTermsApproved) {
    this.webcertUserTermsApproved = webcertUserTermsApproved;
  }
}
