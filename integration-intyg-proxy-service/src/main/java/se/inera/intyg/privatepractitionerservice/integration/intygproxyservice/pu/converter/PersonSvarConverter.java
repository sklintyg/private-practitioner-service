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
package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@Component
public class PersonSvarConverter {

  private static final String SPACE = " ";
  private static final String EMPTY = "";

  public Status convertStatus(StatusDTO statusDTO) {
    return switch (statusDTO) {
      case FOUND -> Status.FOUND;
      case NOT_FOUND -> Status.NOT_FOUND;
      case ERROR -> Status.ERROR;
    };
  }

  public Person convertPerson(PersonDTO personDTO) {
    return Person.builder()
        .personId(personDTO.personnummer())
        .name(buildPersonName(personDTO))
        .build();
  }

  private String buildPersonName(PersonDTO personDTO) {

    return getFornamn(personDTO) + getEfternamn(personDTO);
  }

  private String getFornamn(PersonDTO personDTO) {
    if (personDTO.fornamn() != null && !personDTO.fornamn().isEmpty()) {
      return personDTO.fornamn();
    }
    return EMPTY;
  }

  private String getEfternamn(PersonDTO personDTO) {
    if (personDTO.efternamn() != null && !personDTO.efternamn().isEmpty()) {
      return SPACE + personDTO.efternamn();
    }
    return EMPTY;
  }
}
