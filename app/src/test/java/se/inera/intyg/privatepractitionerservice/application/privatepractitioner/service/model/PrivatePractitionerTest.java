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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeHospPersonBuilder;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrivatePractitionerTest {

  @Test
  void shouldUpdateHospUpdatedUpdateWithHospInformation() {
    final var privatePractitioner = kranstegeBuilder().build();

    final var hosp = kranstegeHospPersonBuilder().hospUpdated(LocalDateTime.now()).build();

    privatePractitioner.updateWithHospInformation(hosp);

    assertEquals(hosp.getHospUpdated(), privatePractitioner.getHospUpdated());
  }

  @Test
  void shouldUpdatePersonalPrescriptionCodeWithHospInformation() {
    final var privatePractitioner = kranstegeBuilder().build();

    final var hosp = kranstegeHospPersonBuilder().personalPrescriptionCode("NEW_CODE").build();

    privatePractitioner.updateWithHospInformation(hosp);

    assertEquals(
        hosp.getPersonalPrescriptionCode(), privatePractitioner.getPersonalPrescriptionCode());
  }

  @Test
  void shouldUpdateSpecialtiesWithHospInformation() {
    final var privatePractitioner = kranstegeBuilder().build();

    final var hosp =
        kranstegeHospPersonBuilder()
            .specialities(
                List.of(new Speciality("NEW_SPECIALITY_CODE", "New Speciality Name", "LK")))
            .build();

    privatePractitioner.updateWithHospInformation(hosp);

    assertEquals(hosp.getSpecialities(), privatePractitioner.getSpecialties());
  }

  @Test
  void shouldUpdateLicensedHealthcareProfessionsWithHospInformation() {
    final var privatePractitioner = kranstegeBuilder().build();

    final var hosp =
        kranstegeHospPersonBuilder()
            .licensedHealthcareProfessions(
                List.of(new LicensedHealtcareProfession("NEW_LICENSE_CODE", "New License Name")))
            .build();

    privatePractitioner.updateWithHospInformation(hosp);

    assertEquals(
        hosp.getLicensedHealthcareProfessions(),
        privatePractitioner.getLicensedHealthcareProfessions());
  }

  @Test
  void shouldClearHospInformationWithHospInformation() {
    final var privatePractitioner = kranstegeBuilder().build();

    final var hosp =
        kranstegeHospPersonBuilder()
            .personalPrescriptionCode(null)
            .licensedHealthcareProfessions(List.of())
            .specialities(List.of())
            .build();

    privatePractitioner.updateWithHospInformation(hosp);

    assertAll(
        () -> assertNull(privatePractitioner.getPersonalPrescriptionCode()),
        () -> assertEquals(List.of(), privatePractitioner.getSpecialties()),
        () -> assertEquals(List.of(), privatePractitioner.getLicensedHealthcareProfessions()));
  }
}
