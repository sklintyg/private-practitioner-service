package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;

import org.junit.jupiter.api.Test;

class PrivatePractitionerConverterTest {

  private final PrivatePractitionerConverter converter = new PrivatePractitionerConverter();

  @Test
  void shouldConvertHsaId() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getHsaId(), dto.getHsaId());
  }

  @Test
  void shouldConvertPersonId() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getPersonId(), dto.getPersonId());
  }

  @Test
  void shouldConvertName() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getName(), dto.getName());
  }

  @Test
  void shouldConvertCareProviderName() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getCareProviderName(), dto.getCareProviderName());
  }

  @Test
  void shouldConvertPosition() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getPosition(), dto.getPosition());
  }

  @Test
  void shouldConvertCareUnitName() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getCareUnitName(), dto.getCareUnitName());
  }

  @Test
  void shouldConvertHealthcareServiceType() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getHealthcareServiceType(), dto.getHealthcareServiceType());
  }

  @Test
  void shouldConvertWorkplaceCode() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getWorkplaceCode(), dto.getWorkplaceCode());
  }

  @Test
  void shouldConvertPhoneNumber() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getPhoneNumber(), dto.getPhoneNumber());
  }

  @Test
  void shouldConvertEmail() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getEmail(), dto.getEmail());
  }

  @Test
  void shouldConvertAddress() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getAddress(), dto.getAddress());
  }

  @Test
  void shouldConvertZipCode() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getZipCode(), dto.getZipCode());
  }

  @Test
  void shouldConvertCity() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getCity(), dto.getCity());
  }

  @Test
  void shouldConvertMunicipality() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getMunicipality(), dto.getMunicipality());
  }

  @Test
  void shouldConvertCounty() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getCounty(), dto.getCounty());
  }

  @Test
  void shouldConvertPersonalPrescriptionCode() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getPersonalPrescriptionCode(), dto.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(2, dto.getSpecialties().size());
    assertEquals(DR_KRANSTEGE.getSpecialties().getFirst().code(),
        dto.getSpecialties().getFirst().code());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(1, dto.getLicensedHealthcareProfessions().size());
    assertEquals(DR_KRANSTEGE.getLicensedHealthcareProfessions().getFirst().code(),
        dto.getLicensedHealthcareProfessions().getFirst().code());
  }

  @Test
  void shouldConvertRegistrationDate() {
    final var dto = converter.convert(DR_KRANSTEGE);
    assertEquals(DR_KRANSTEGE.getRegistrationDate(), dto.getRegistrationDate());
  }
}