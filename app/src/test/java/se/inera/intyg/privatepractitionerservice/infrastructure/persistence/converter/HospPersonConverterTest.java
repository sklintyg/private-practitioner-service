package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_CREDENTIALS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.kranstegeHospCredentialsBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;

@ExtendWith(MockitoExtension.class)
class HospPersonConverterTest {

  @Mock
  private HashUtility hashUtility;
  @InjectMocks
  private HospPersonConverter hospPersonConverter;

  @Nested
  class HospConverterTest {

    @Test
    void shouldReturnEmptyIfHospCredentialsIsNull() {
      final var actual = hospPersonConverter.convert(null);
      assertTrue(actual.isEmpty(), "Expected empty when hosp credentials is null");
    }

    @Test
    void shouldReturnEmptyIfHospCredentialsPersonNumberIsNull() {
      final var actual = hospPersonConverter.convert(
          HospCredentialsForPerson.builder()
              .personalIdentityNumber(null)
              .build()
      );
      assertTrue(actual.isEmpty(), "Expected empty when person number is null");
    }

    @Test
    void shouldReturnHospPersonIfHospCredentialsExist() {
      assertEquals(HospPerson.class,
          hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS).orElseThrow().getClass());
    }

    @Test
    void shouldConvertHospCredentials() {

      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(DR_KRANSTEGE_HOSP_PERSON, actual.orElseThrow());
    }
  }

  @Nested
  class HealthcareProfessionConverterTest {

    @Test
    void shouldConvertLicensedHealthcareProfessions() {
      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS).orElseThrow();

      assertEquals(DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS,
          actual.getLicensedHealthcareProfessions());
    }

    @Test
    void shouldReturnEmptyListIfLicensedHealthcareProfessionsIsNull() {
      final var hospCredentials = kranstegeHospCredentialsBuilder().healthCareProfessionalLicence(
          null).build();

      final var actual = hospPersonConverter.convert(hospCredentials).orElseThrow();

      assertTrue(actual.getLicensedHealthcareProfessions().isEmpty(),
          "Expected empty when HealthcareProfessions is null");
    }

    @Test
    void shouldFilterOutNonValidHealthcareProfessions() {
      final var hospCredentials = kranstegeHospCredentialsBuilder()
          .healthCareProfessionalLicence(
              List.of(
                  HealthCareProfessionalLicence.builder()
                      .healthCareProfessionalLicenceCode("")
                      .healthCareProfessionalLicenceName(null)
                      .build()
              )
          )
          .build();

      final var actual = hospPersonConverter.convert(hospCredentials).orElseThrow();

      assertTrue(actual.getLicensedHealthcareProfessions().isEmpty(),
          "Expected non-valid HealthcareProfession to be filtered out");
    }
  }

  @Nested
  class SpecialityConverterTest {

    @Test
    void shouldConvertSpecialities() {
      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(DR_KRANSTEGE_SPECIALITIES, actual.orElseThrow().getSpecialities());
    }

    @Test
    void shouldReturnEmptyListIfHCPSpecialityCodesIsNull() {
      final var hospCredentials = kranstegeHospCredentialsBuilder().healthCareProfessionalLicenceSpeciality(
          null).build();

      final var actual = hospPersonConverter.convert(hospCredentials).orElseThrow();

      assertTrue(actual.getSpecialities().isEmpty(),
          "Expected empty specialities when HCPSpecialityCodes is null");

    }

    @Test
    void shouldFilterOutNonValidSpecialities() {
      final var hospCredentials = kranstegeHospCredentialsBuilder()
          .healthCareProfessionalLicenceSpeciality(
              List.of(
                  HCPSpecialityCodes.builder()
                      .specialityCode("")
                      .specialityName(null)
                      .build()
              )
          )
          .build();

      final var actual = hospPersonConverter.convert(hospCredentials).orElseThrow();

      assertTrue(actual.getSpecialities().isEmpty(),
          "Expected non-valid HCPSpecialityCodes to be filtered out");
    }

    @Test
    void shouldFilterOutNonPhysicianSpecialities() {
      final var hospCredentials = kranstegeHospCredentialsBuilder()
          .healthCareProfessionalLicenceSpeciality(
              List.of(
                  HCPSpecialityCodes.builder()
                      .healthCareProfessionalLicenceCode("DT")
                      .specialityName("Dietistens specialitet")
                      .specialityCode("73")
                      .build()
              )
          )
          .build();

      final var actual = hospPersonConverter.convert(hospCredentials).orElseThrow();

      assertTrue(actual.getSpecialities().isEmpty(),
          "Expected non physician specialities to be filtered out");
    }
  }
}