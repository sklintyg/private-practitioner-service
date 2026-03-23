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
package se.inera.intyg.privatepractitionerservice.testdata;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

public class TestDataAssert {

  public static void assertPrivatlakareEntity(
      PrivatlakareEntity expected, PrivatlakareEntity actual) {
    assertAll(
        () -> assertEquals(expected.getPrivatlakareId(), actual.getPrivatlakareId()),
        () -> assertEquals(expected.getPersonId(), actual.getPersonId()),
        () -> assertEquals(expected.getHsaId(), actual.getHsaId()),
        () -> assertEquals(expected.getFullstandigtNamn(), actual.getFullstandigtNamn()),
        () -> assertEquals(expected.getForskrivarKod(), actual.getForskrivarKod()),
        () -> assertEquals(expected.isGodkandAnvandare(), actual.isGodkandAnvandare()),
        () -> assertEquals(expected.getEnhetsId(), actual.getEnhetsId()),
        () -> assertEquals(expected.getEnhetsNamn(), actual.getEnhetsNamn()),
        () -> assertEquals(expected.getArbetsplatsKod(), actual.getArbetsplatsKod()),
        () -> assertEquals(expected.getAgarform(), actual.getAgarform()),
        () -> assertEquals(expected.getPostadress(), actual.getPostadress()),
        () -> assertEquals(expected.getPostnummer(), actual.getPostnummer()),
        () -> assertEquals(expected.getPostort(), actual.getPostort()),
        () -> assertEquals(expected.getTelefonnummer(), actual.getTelefonnummer()),
        () -> assertEquals(expected.getEpost(), actual.getEpost()),
        () -> assertEquals(expected.getEnhetStartdatum(), actual.getEnhetStartdatum()),
        () -> assertEquals(expected.getEnhetSlutDatum(), actual.getEnhetSlutDatum()),
        () -> assertEquals(expected.getLan(), actual.getLan()),
        () -> assertEquals(expected.getKommun(), actual.getKommun()),
        () -> assertEquals(expected.getVardgivareId(), actual.getVardgivareId()),
        () -> assertEquals(expected.getVardgivareNamn(), actual.getVardgivareNamn()),
        () -> assertEquals(expected.getVardgivareStartdatum(), actual.getVardgivareStartdatum()),
        () -> assertEquals(expected.getVardgivareSlutdatum(), actual.getVardgivareSlutdatum()),
        () -> assertEquals(expected.getBefattningar(), actual.getBefattningar()),
        () ->
            assertEquals(
                expected.getLegitimeradeYrkesgrupper(), actual.getLegitimeradeYrkesgrupper()),
        () -> assertEquals(expected.getSpecialiteter(), actual.getSpecialiteter()),
        () -> assertEquals(expected.getVerksamhetstyper(), actual.getVerksamhetstyper()),
        () -> assertEquals(expected.getVardformer(), actual.getVardformer()),
        () ->
            assertEquals(expected.getSenasteHospUppdatering(), actual.getSenasteHospUppdatering()),
        () -> assertNotNull(actual.getRegistreringsdatum()));
  }

  private TestDataAssert() {
    throw new IllegalStateException("Utility class");
  }
}
