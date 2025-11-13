package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ADDRESS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_CARE_UNIT_NAME;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_CITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_COUNTY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_MUNICIPALITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_NAME;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_OWNERSHIP_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PHONE_NUMBER;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_POSITION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_WORKPLACE_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ZIP_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REQUEST;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerFactoryTest {

  @InjectMocks
  private PrivatePractitionerFactory privatePractitionerFactory;

  @Test
  void shouldIncludePersonId() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_PERSON_ID, actual.getPersonId());
  }

  @Test
  void shouldIncludeName() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_NAME, actual.getName());
  }

  @Test
  void shouldIncludePosition() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_POSITION, actual.getPosition());
  }

  @Test
  void shouldIncludeCareProviderName() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_CARE_UNIT_NAME, actual.getCareProviderName());
  }

  @Test
  void shouldIncludeOwnershipType() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_OWNERSHIP_TYPE, actual.getOwnershipType());
  }

  @Test
  void shouldIncludeTypeOfCare() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_TYPE_OF_CARE, actual.getTypeOfCare());
  }

  @Test
  void shouldIncludeHealthcareServiceType() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE, actual.getHealthcareServiceType());
  }

  @Test
  void shouldIncludeWorkplaceCode() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_WORKPLACE_CODE, actual.getWorkplaceCode());
  }

  @Test
  void shouldIncludePhoneNumber() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_PHONE_NUMBER, actual.getPhoneNumber());
  }

  @Test
  void shouldIncludeEmail() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_EMAIL, actual.getEmail());
  }

  @Test
  void shouldIncludeAddress() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_ADDRESS, actual.getAddress());
  }

  @Test
  void shouldIncludeZipCode() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_ZIP_CODE, actual.getZipCode());
  }

  @Test
  void shouldIncludeCity() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_CITY, actual.getCity());
  }

  @Test
  void shouldIncludeMunicipality() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_MUNICIPALITY, actual.getMunicipality());
  }

  @Test
  void shouldIncludeCounty() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertEquals(DR_KRANSTEGE_COUNTY, actual.getCounty());
  }

  @Test
  void shouldIncludeRegistrationDate() {
    final var actual = privatePractitionerFactory.create(DR_KRANSTEGE_REQUEST);
    assertTrue(
        Math.abs(ChronoUnit.SECONDS.between(
            LocalDateTime.now(),
            actual.getRegistrationDate()
        )) <= 1,
        "Registreringsdatum should be within 1 second"
    );
  }
}