package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    assertEquals(kranstegeBuilder().build().getHealthcareServiceType(),
        dto.getHealthcareServiceType());
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
    assertEquals(kranstegeBuilder().build().getPersonalPrescriptionCode(),
        dto.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(2, dto.getSpecialties().size());
    assertEquals(kranstegeBuilder().build().getSpecialties().getFirst().code(),
        dto.getSpecialties().getFirst().code());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(1, dto.getLicensedHealthcareProfessions().size());
    assertEquals(kranstegeBuilder().build().getLicensedHealthcareProfessions().getFirst().code(),
        dto.getLicensedHealthcareProfessions().getFirst().code());
  }

  @Test
  void shouldConvertRegistrationDate() {
    final var dto = converter.convert(kranstegeBuilder().build());
    assertEquals(kranstegeBuilder().build().getRegistrationDate(), dto.getRegistrationDate());
  }
}