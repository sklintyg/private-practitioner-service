package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.DR_KRANSTEGE_ENTITY;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrivatlakareEntityConverterTest {

  @InjectMocks
  private PrivatlakareEntityConverter converter;

  @Test
  void shouldConvertCareUnitName() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getEnhetsNamn(), actual.getCareUnitName());
  }

  @Test
  void shouldConvertPosition() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getBefattningar().getFirst().getKod(), actual.getPosition());
  }

  @Test
  void shouldConvertWorkplaceCode() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getArbetsplatsKod(), actual.getWorkplaceCode());
  }

  @Test
  void shouldConvertOwnershipType() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getAgarform(), actual.getOwnershipType());
  }

  @Test
  void shouldConvertTypeOfCare() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getVardformer().getFirst().getKod(), actual.getTypeOfCare());
  }

  @Test
  void shouldConvertHealthcareServiceType() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getVerksamhetstyper().getFirst().getKod(),
        actual.getHealthcareServiceType());
  }

  @Test
  void shouldConvertPhoneNumber() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getTelefonnummer(), actual.getPhoneNumber());
  }

  @Test
  void shouldConvertAddress() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getPostadress(), actual.getAddress());
  }

  @Test
  void shouldConvertZipCode() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getPostnummer(), actual.getZipCode());
  }

  @Test
  void shouldConvertCity() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getPostort(), actual.getCity());
  }

  @Test
  void shouldConvertMunicipality() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getKommun(), actual.getMunicipality());
  }

  @Test
  void shouldConvertCounty() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getLan(), actual.getCounty());
  }

  @Test
  void shouldConvertPersonalPrescriptionCode() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getForskrivarKod(), actual.getPersonalPrescriptionCode());
  }

  @Test
  void shouldConvertSpecialties() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(2, actual.getSpecialties().size());
    assertEquals(DR_KRANSTEGE_ENTITY.getSpecialiteter().getFirst().getKod(),
        actual.getSpecialties().getFirst().code());
    assertEquals(DR_KRANSTEGE_ENTITY.getSpecialiteter().getFirst().getNamn(),
        actual.getSpecialties().getFirst().name());
    assertEquals(DR_KRANSTEGE_ENTITY.getSpecialiteter().getLast().getKod(),
        actual.getSpecialties().getLast().code());
    assertEquals(DR_KRANSTEGE_ENTITY.getSpecialiteter().getLast().getNamn(),
        actual.getSpecialties().getLast().name());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(1, actual.getLicensedHealthcareProfessions().size());
    assertEquals(DR_KRANSTEGE_ENTITY.getLegitimeradeYrkesgrupper().getFirst().getKod(),
        actual.getLicensedHealthcareProfessions().getFirst().code());
    assertEquals(DR_KRANSTEGE_ENTITY.getLegitimeradeYrkesgrupper().getFirst().getNamn(),
        actual.getLicensedHealthcareProfessions().getFirst().name());
  }

  @Test
  void shouldConvertStartDate() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getEnhetStartdatum(), actual.getStartDate());
  }

  @Test
  void shouldConvertEndDate() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getEnhetSlutDatum(), actual.getEndDate());
  }

  @Test
  void shouldConvertHospUpdated() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getSenasteHospUppdatering(), actual.getHospUpdated());
  }
}