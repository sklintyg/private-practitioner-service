package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_RESTRICTIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.DR_KRANSTEGE_ENTITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.kranstegeEntityBuilder;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.EpostEntity;

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
    assertThat(DR_KRANSTEGE_SPECIALITIES).containsExactlyInAnyOrderElementsOf(
        actual.getSpecialties());
  }

  @Test
  void shouldConvertRestrictions() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertThat(DR_KRANSTEGE_RESTRICTIONS).containsExactlyInAnyOrderElementsOf(
        actual.getRestrictions());
  }

  @Test
  void shouldConvertLicensedHealthcareProfessions() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertThat(DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS).containsExactlyInAnyOrderElementsOf(
        actual.getLicensedHealthcareProfessions());
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

  @Test
  void shouldConvertHsaId() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getHsaId(), actual.getHsaId());
  }

  @Test
  void shouldConvertPersonId() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getPersonId(), actual.getPersonId());
  }

  @Test
  void shouldConvertName() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getFullstandigtNamn(), actual.getName());
  }

  @Test
  void shouldConvertCareProviderName() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getVardgivareNamn(), actual.getCareProviderName());
  }

  @Test
  void shouldConvertEmail() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getEpost(), actual.getEmail());
  }

  @Test
  void shouldConvertRegistrationDate() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(DR_KRANSTEGE_ENTITY.getRegistreringsdatum(), actual.getRegistrationDate());
  }

  @Test
  void shouldConvertEmailCount() {
    var actual = converter.convert(kranstegeEntityBuilder()
        .epostList(List.of(EpostEntity.builder().build(), EpostEntity.builder().build()))
        .build());
    assertEquals(2, actual.getEmailCount());
  }

  @Test
  void shouldConvertEmailCountWhenNoEmails() {
    var actual = converter.convert(DR_KRANSTEGE_ENTITY);
    assertEquals(0, actual.getEmailCount()
    );
  }
}