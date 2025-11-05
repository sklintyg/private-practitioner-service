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
package se.inera.intyg.privatlakarportal.auth;

/**
 * Created by pebe on 2015-08-11.
 */
public class PrivatlakarUser {

  private static final long serialVersionUID = 8711015219408194075L;
  private static final int THIRTYONE = 31;

  private final String personalIdentityNumber;
  private String name;
  private final String authenticationScheme;
  private boolean nameFromPuService;

  public PrivatlakarUser(String personalIdentityNumber, String name, String authenticationScheme) {
    this.personalIdentityNumber = personalIdentityNumber;
    this.authenticationScheme = authenticationScheme;
    this.name = name;
    nameFromPuService = false;
  }

  public String getPersonalIdentityNumber() {
    return personalIdentityNumber;
  }

  public String getName() {
    return name;
  }

  public String getAuthenticationScheme() {
    return authenticationScheme;
  }

  public boolean isNameFromPuService() {
    return nameFromPuService;
  }

  public void updateNameFromPuService(String name) {
    this.name = name;
    nameFromPuService = true;
  }

  // CHECKSTYLE:OFF NeedBraces
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PrivatlakarUser)) {
      return false;
    }

    PrivatlakarUser that = (PrivatlakarUser) o;

    if (nameFromPuService != that.nameFromPuService) {
      return false;
    }
    if (!personalIdentityNumber.equals(that.personalIdentityNumber)) {
      return false;
    }
    if (!name.equals(that.name)) {
      return false;
    }
    return authenticationScheme.equals(that.authenticationScheme);
  }
  // CHECKSTYLE:ON NeedBraces

  @Override
  public int hashCode() {
    int result = personalIdentityNumber.hashCode();
    result = THIRTYONE * result + name.hashCode();
    result = THIRTYONE * result + authenticationScheme.hashCode();
    result = THIRTYONE * result + (nameFromPuService ? 1 : 0);
    return result;
  }

  public String getRelyingPartyRegistrationId() {
    return null;
  }
}
