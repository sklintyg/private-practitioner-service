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
package se.inera.intyg.privatlakarportal.common.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegistrationTest {

  @Test
  public void testRegistrationIsValid() {
    Registration registration = new Registration();
    registration.setAdress("Testadress");
    registration.setAgarForm("Testagarform");
    registration.setArbetsplatskod("Kod");
    registration.setBefattning("Befattning");
    registration.setEpost("test@test.se");
    registration.setKommun("Kommun");
    registration.setLan("Län");
    registration.setPostnummer("12345");
    registration.setPostort("postort");
    registration.setTelefonnummer("12343455");
    registration.setVardform("Vårdform");
    registration.setVerksamhetensNamn("Verksamhetsnamn");
    registration.setVerksamhetstyp("Typ");

    assertTrue(registration.checkIsValid());
  }

}
