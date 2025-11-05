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
package se.inera.intyg.privatepractitionerservice.application.service.model;

import java.time.LocalDateTime;

public class PrivatePractitioner {

  private final String hsaId;
  private final String personId;
  private final String name;
  private final String careproviderName;
  private final String email;
  private final LocalDateTime registrationDate;

  public PrivatePractitioner(String hsaId, String personId, String name, String careproviderName,
      String email,
      LocalDateTime registrationDate) {
    this.hsaId = hsaId;
    this.personId = personId;
    this.name = name;
    this.careproviderName = careproviderName;
    this.email = email;
    this.registrationDate = registrationDate;
  }

  public String getHsaId() {
    return hsaId;
  }

  public String getPersonId() {
    return personId;
  }

  public String getName() {
    return name;
  }

  public String getCareproviderName() {
    return careproviderName;
  }

  public String getEmail() {
    return email;
  }

  public LocalDateTime getRegistrationDate() {
    return registrationDate;
  }
}
