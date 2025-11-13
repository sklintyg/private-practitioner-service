package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;

class PrivatePractitionerConverterTest {

  private final PrivatePractitionerConverter converter = new PrivatePractitionerConverter();

  @Test
  void shouldConvertHsaId() {
    final var expected = "HSA123";
    final var model = PrivatePractitioner.builder().hsaId(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getHsaId());
  }

  @Test
  void shouldConvertPersonId() {
    final var expected = "197001011234";
    final var model = PrivatePractitioner.builder().personId(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getPersonId());
  }

  @Test
  void shouldConvertName() {
    final var expected = "Dr Test";
    final var model = PrivatePractitioner.builder().name(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getName());
  }

  @Test
  void shouldConvertCareProviderName() {
    final var expected = "CareProvider";
    final var model = PrivatePractitioner.builder().careProviderName(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getCareProviderName());
  }

  @Test
  void shouldConvertPosition() {
    final var expected = "POS";
    final var model = PrivatePractitioner.builder().position(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getPosition());
  }

  @Test
  void shouldConvertCareUnitName() {
    final var expected = "CareUnit";
    final var model = PrivatePractitioner.builder().careUnitName(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getCareUnitName());
  }

  @Test
  void shouldConvertOwnershipType() {
    final var expected = "OwnerType";
    final var model = PrivatePractitioner.builder().ownershipType(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getOwnershipType());
  }

  @Test
  void shouldConvertTypeOfCare() {
    final var expected = "TypeOfCare";
    final var model = PrivatePractitioner.builder().typeOfCare(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getTypeOfCare());
  }

  @Test
  void shouldConvertHealthcareServiceType() {
    final var expected = "HST";
    final var model = PrivatePractitioner.builder().healthcareServiceType(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getHealthcareServiceType());
  }

  @Test
  void shouldConvertWorkplaceCode() {
    final var expected = "WP123";
    final var model = PrivatePractitioner.builder().workplaceCode(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getWorkplaceCode());
  }

  @Test
  void shouldConvertPhoneNumber() {
    final var expected = "0700000000";
    final var model = PrivatePractitioner.builder().phoneNumber(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getPhoneNumber());
  }

  @Test
  void shouldConvertEmail() {
    final var expected = "dr@test.local";
    final var model = PrivatePractitioner.builder().email(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getEmail());
  }

  @Test
  void shouldConvertAddress() {
    final var expected = "Street 1";
    final var model = PrivatePractitioner.builder().address(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getAddress());
  }

  @Test
  void shouldConvertZipCode() {
    final var expected = "12345";
    final var model = PrivatePractitioner.builder().zipCode(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getZipCode());
  }

  @Test
  void shouldConvertCity() {
    final var expected = "City";
    final var model = PrivatePractitioner.builder().city(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getCity());
  }

  @Test
  void shouldConvertMunicipality() {
    final var expected = "Municipality";
    final var model = PrivatePractitioner.builder().municipality(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getMunicipality());
  }

  @Test
  void shouldConvertCounty() {
    final var expected = "County";
    final var model = PrivatePractitioner.builder().county(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getCounty());
  }

  @Test
  void shouldConvertPersonalPrescriptionCode() {
    final var expected = "PPCODE";
    final var model = PrivatePractitioner.builder().personalPrescriptionCode(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    final var expected = List.of(new Speciality("SC", "Special"));
    final var model = PrivatePractitioner.builder().specialties(expected).build();
    final var dto = converter.convert(model);
    assertEquals(1, dto.getSpecialties().size());
    assertEquals(expected.get(0).code(), dto.getSpecialties().get(0).getCode());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    final var expected = List.of(new LicensedHealtcareProfession("LC", "Läkare"));
    final var model = PrivatePractitioner.builder().licensedHealthcareProfessions(expected).build();
    final var dto = converter.convert(model);
    assertEquals(1, dto.getLicensedHealthcareProfessions().size());
    assertEquals(expected.get(0).code(), dto.getLicensedHealthcareProfessions().get(0).getCode());
  }

  @Test
  void shouldConvertStartDate() {
    final var expected = LocalDateTime.now().minusDays(10);
    final var model = PrivatePractitioner.builder().startDate(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getStartDate());
  }

  @Test
  void shouldConvertEndDate() {
    final var expected = LocalDateTime.now().plusDays(10);
    final var model = PrivatePractitioner.builder().endDate(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getEndDate());
  }

  @Test
  void shouldConvertRegistrationDate() {
    final var expected = LocalDateTime.now().minusDays(20);
    final var model = PrivatePractitioner.builder().registrationDate(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getRegistrationDate());
  }

  @Test
  void shouldConvertHospUpdated() {
    final var expected = LocalDateTime.now().minusDays(1);
    final var model = PrivatePractitioner.builder().hospUpdated(expected).build();
    final var dto = converter.convert(model);
    assertEquals(expected, dto.getHospUpdated());
  }
}