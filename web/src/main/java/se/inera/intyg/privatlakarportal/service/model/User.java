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
package se.inera.intyg.privatlakarportal.service.model;

import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.privatlakarportal.auth.PrivatlakarUser;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;

/**
 * Created by pebe on 2015-08-25.
 */
public class User {

  private String personalIdentityNumber;
  private String name;
  private boolean nameFromPuService;
  private boolean nameUpdated;
  private String authenticationScheme;
  private PersonSvar.Status personSvarStatus;
  private RegistrationStatus status;

  public User(PrivatlakarUser privatlakarUser, PersonSvar.Status personSvarStatus,
      RegistrationStatus status, boolean nameUpdated) {
    personalIdentityNumber = privatlakarUser.getPersonalIdentityNumber();
    name = privatlakarUser.getName();
    nameFromPuService = privatlakarUser.isNameFromPuService();
    authenticationScheme = privatlakarUser.getAuthenticationScheme();
    this.personSvarStatus = personSvarStatus;
    this.status = status;
    this.nameUpdated = nameUpdated;
  }

  public String getPersonalIdentityNumber() {
    return personalIdentityNumber;
  }

  public String getName() {
    return name;
  }

  public boolean isNameFromPuService() {
    return nameFromPuService;
  }

  public boolean isNameUpdated() {
    return nameUpdated;
  }

  public String getAuthenticationScheme() {
    return authenticationScheme;
  }

  public PersonSvar.Status getPersonSvarStatus() {
    return personSvarStatus;
  }

  public RegistrationStatus getStatus() {
    return status;
  }

}
