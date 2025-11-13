package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.BefattningEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VardformEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VerksamhetstypEntity;

@ExtendWith(MockitoExtension.class)
class PrivatlakareEntityConverterTest {

  @InjectMocks
  private PrivatlakareEntityConverter converter;

  @Test
  void shouldConvertCareUnitName() {
    final var expected = "UnitName";
    var entity = PrivatlakareEntity.builder().enhetsNamn(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getCareUnitName());
  }

  @Test
  void shouldConvertPosition() {
    final var expected = "POS";
    var entity = PrivatlakareEntity.builder().befattningar(List.of(new BefattningEntity(expected))).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getPosition());
  }

  @Test
  void shouldConvertWorkplaceCode() {
    final var expected = "WP";
    var entity = PrivatlakareEntity.builder().arbetsplatsKod(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getWorkplaceCode());
  }

  @Test
  void shouldConvertOwnershipType() {
    final var expected = "Owner";
    var entity = PrivatlakareEntity.builder().agarform(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getOwnershipType());
  }

  @Test
  void shouldConvertTypeOfCare() {
    final var expected = "TOC";
    var entity = PrivatlakareEntity.builder().vardformer(List.of(new VardformEntity(expected))).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getTypeOfCare());
  }

  @Test
  void shouldConvertHealthcareServiceType() {
    final var expected = "HST";
    var entity = PrivatlakareEntity.builder().verksamhetstyper(List.of(new VerksamhetstypEntity(expected))).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getHealthcareServiceType());
  }

  @Test
  void shouldConvertPhoneNumber() {
    final var expected = "070-111";
    var entity = PrivatlakareEntity.builder().telefonnummer(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getPhoneNumber());
  }

  @Test
  void shouldConvertAddress() {
    final var expected = "Addr";
    var entity = PrivatlakareEntity.builder().postadress(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getAddress());
  }

  @Test
  void shouldConvertZipCode() {
    final var expected = "ZIP";
    var entity = PrivatlakareEntity.builder().postnummer(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getZipCode());
  }

  @Test
  void shouldConvertCity() {
    final var expected = "CityX";
    var entity = PrivatlakareEntity.builder().postort(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getCity());
  }

  @Test
  void shouldConvertMunicipality() {
    final var expected = "MunicipalX";
    var entity = PrivatlakareEntity.builder().kommun(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getMunicipality());
  }

  @Test
  void shouldConvertCounty() {
    final var expected = "CountyX";
    var entity = PrivatlakareEntity.builder().lan(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getCounty());
  }

  @Test
  void shouldConvertPersonalPrescriptionCode() {
    final var expected = "PPCODE";
    var entity = PrivatlakareEntity.builder().forskrivarKod(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    final var expected = new SpecialitetEntity("SNAME", "SCODE");
    var entity = PrivatlakareEntity.builder().specialiteter(List.of(expected)).build();
    var actual = converter.convert(entity);
    assertEquals(1, actual.getSpecialties().size());
    assertEquals(expected.getKod(), actual.getSpecialties().getFirst().code());
    assertEquals(expected.getNamn(), actual.getSpecialties().getFirst().name());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    final var expected = new LegitimeradYrkesgruppEntity("LNAME", "LCODE");
    var entity = PrivatlakareEntity.builder().legitimeradeYrkesgrupper(List.of(expected)).build();
    var actual = converter.convert(entity);
    assertEquals(1, actual.getLicensedHealthcareProfessions().size());
    assertEquals(expected.getKod(), actual.getLicensedHealthcareProfessions().getFirst().code());
    assertEquals(expected.getNamn(), actual.getLicensedHealthcareProfessions().getFirst().name());
  }

  @Test
  void shouldConvertStartDate() {
    final var expected = LocalDateTime.now().minusDays(2);
    var entity = PrivatlakareEntity.builder().vardgivareStartdatum(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getStartDate());
  }

  @Test
  void shouldConvertEndDate() {
    final var expected = LocalDateTime.now().plusDays(2);
    var entity = PrivatlakareEntity.builder().vardgivareSlutdatum(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getEndDate());
  }

  @Test
  void shouldConvertHospUpdated() {
    final var expected = LocalDateTime.now().minusHours(1);
    var entity = PrivatlakareEntity.builder().senasteHospUppdatering(expected).build();
    var actual = converter.convert(entity);
    assertEquals(expected, actual.getHospUpdated());
  }
}