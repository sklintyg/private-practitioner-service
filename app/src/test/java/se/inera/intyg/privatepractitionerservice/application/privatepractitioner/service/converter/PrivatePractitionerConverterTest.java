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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_REGISTRATION_DATE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import org.junit.jupiter.api.Test;

class PrivatePractitionerConverterTest {

  private final PrivatePractitionerConverter converter = new PrivatePractitionerConverter();

  @Test
  void shouldConvertHsaId() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getHsaId(), dto.getHsaId());
  }

  @Test
  void shouldConvertPersonId() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getPersonId(), dto.getPersonId());
  }

  @Test
  void shouldConvertName() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getName(), dto.getName());
  }

  @Test
  void shouldConvertCareProviderName() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getCareProviderName(), dto.getCareProviderName());
  }

  @Test
  void shouldConvertPosition() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getPosition(), dto.getPosition());
  }

  @Test
  void shouldConvertCareUnitName() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getCareUnitName(), dto.getCareUnitName());
  }

  @Test
  void shouldConvertHealthcareServiceType() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(
        kranstegeBuilder().build().getHealthcareServiceType(), dto.getHealthcareServiceType());
  }

  @Test
  void shouldConvertWorkplaceCode() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getWorkplaceCode(), dto.getWorkplaceCode());
  }

  @Test
  void shouldConvertPhoneNumber() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getPhoneNumber(), dto.getPhoneNumber());
  }

  @Test
  void shouldConvertEmail() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getEmail(), dto.getEmail());
  }

  @Test
  void shouldConvertAddress() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getAddress(), dto.getAddress());
  }

  @Test
  void shouldConvertZipCode() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getZipCode(), dto.getZipCode());
  }

  @Test
  void shouldConvertCity() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getCity(), dto.getCity());
  }

  @Test
  void shouldConvertMunicipality() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getMunicipality(), dto.getMunicipality());
  }

  @Test
  void shouldConvertCounty() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getCounty(), dto.getCounty());
  }

  @Test
  void shouldConvertPersonalPrescriptionCode() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(
        kranstegeBuilder().build().getPersonalPrescriptionCode(),
        dto.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(2, dto.getSpecialties().size());
    assertEquals(
        kranstegeBuilder().build().getSpecialties().getFirst().code(),
        dto.getSpecialties().getFirst().code());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(1, dto.getLicensedHealthcareProfessions().size());
    assertEquals(
        kranstegeBuilder().build().getLicensedHealthcareProfessions().getFirst().code(),
        dto.getLicensedHealthcareProfessions().getFirst().code());
  }

  @Test
  void shouldConvertRegistrationDate() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE_REGISTRATION_DATE, dto.getRegistrationDate());
  }

  @Test
  void shouldConvertTypeOfCare() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE_TYPE_OF_CARE, dto.getTypeOfCare());
  }
}
