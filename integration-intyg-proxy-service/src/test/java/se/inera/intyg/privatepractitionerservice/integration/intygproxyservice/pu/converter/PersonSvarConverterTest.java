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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

class PersonSvarConverterTest {

  private PersonSvarConverter converter;

  private static final String PERSON_ID = "197705232382";
  public static final String KRANSTEGE_FIRST_NAME = "Frida";
  public static final String KRANSTEGE_LAST_NAME = "Kranstege";

  @BeforeEach
  void setUp() {
    converter = new PersonSvarConverter();
  }

  @Nested
  class ConvertStatus {

    @Test
    void shouldConvertFoundStatus() {
      final var result = converter.convertStatus(StatusDTO.FOUND);
      assertEquals(Status.FOUND, result);
    }

    @Test
    void shouldConvertNotFoundStatus() {
      final var result = converter.convertStatus(StatusDTO.NOT_FOUND);
      assertEquals(Status.NOT_FOUND, result);
    }

    @Test
    void shouldConvertErrorStatus() {
      final var result = converter.convertStatus(StatusDTO.ERROR);
      assertEquals(Status.ERROR, result);
    }

    @Test
    void shouldReturnStatusObject() {
      final var result = converter.convertStatus(StatusDTO.FOUND);
      assertEquals(Status.class, result.getClass());
    }
  }

  @Nested
  class ConvertPerson {

    @Test
    void shouldConvertPersonWithAllNameParts() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals("Frida Kranstege", result.getName());
    }

    @Test
    void shouldConvertPersonWithMissingLastName() {
      final var personDTO = new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, null);
      final var result = converter.convertPerson(personDTO);
      assertEquals(KRANSTEGE_FIRST_NAME, result.getName());
    }

    @Test
    void shouldConvertPersonId() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals(PERSON_ID, result.getPersonId());
    }

    @Test
    void shouldReturnPersonObject() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals(Person.class, result.getClass());
    }

    private static PersonDTO getPersonDTO() {
      return new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, KRANSTEGE_LAST_NAME);
    }
  }
}
