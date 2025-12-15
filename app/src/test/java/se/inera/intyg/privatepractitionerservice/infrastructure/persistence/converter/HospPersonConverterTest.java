package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_RESTRICTIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_CREDENTIALS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.kranstegeHospCredentialsBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeHospPersonBuilder;

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
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson.RestrictionDTO;

@ExtendWith(MockitoExtension.class)
class HospPersonConverterTest {

  @Mock
  private HashUtility hashUtility;
  @InjectMocks
  private HospPersonConverter hospPersonConverter;

  @Nested
  class HospConverterTest {


    @Test
    void shouldReturnEmptyIfHospCredentialsPersonNumberIsNull() {
      final var actual = hospPersonConverter.convert(
          HospCredentialsForPerson.builder()
              .personalIdentityNumber(null)
              .build()
      );
      assertEquals(HospPerson.builder().build(), actual);
    }

    @Test
    void shouldReturnHospPersonIfHospCredentialsExist() {
      assertEquals(HospPerson.class,
          hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS).getClass());
    }

    @Test
    void shouldConvertHospCredentials() {

      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(kranstegeHospPersonBuilder()
          .hospUpdated(null)
          .build(), actual);
    }
  }

  @Nested
  class HealthcareProfessionConverterTest {

    @Test
    void shouldConvertLicensedHealthcareProfessions() {
      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS,
          actual.getLicensedHealthcareProfessions());
    }

    @Test
    void shouldReturnEmptyListIfLicensedHealthcareProfessionsIsNull() {
      final var hospCredentials = kranstegeHospCredentialsBuilder().healthCareProfessionalLicence(
          null).build();

      final var actual = hospPersonConverter.convert(hospCredentials);

      assertTrue(actual.getLicensedHealthcareProfessions().isEmpty(),
          "Expected empty when HealthcareProfessions is null");
    }

    @Test
    void shouldThrowExceptionIfInvalidHealthcareProfessions() {
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

      assertThrows(IllegalStateException.class, () -> hospPersonConverter.convert(hospCredentials));
    }
  }

  @Nested
  class SpecialityConverterTest {

    @Test
    void shouldConvertSpecialities() {
      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(DR_KRANSTEGE_SPECIALITIES, actual.getSpecialities());
    }

    @Test
    void shouldReturnEmptyListIfHCPSpecialityCodesIsNull() {
      final var hospCredentials = kranstegeHospCredentialsBuilder().healthCareProfessionalLicenceSpeciality(
          null).build();

      final var actual = hospPersonConverter.convert(hospCredentials);

      assertTrue(actual.getSpecialities().isEmpty(),
          "Expected empty specialities when HCPSpecialityCodes is null");

    }

    @Test
    void shouldThrowExceptionIfInvalidSpecialities() {
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

      assertThrows(IllegalStateException.class, () -> hospPersonConverter.convert(hospCredentials));
    }
  }

  @Nested
  class RestrictionConverterTest {

    @Test
    void shouldConvertRestrictions() {
      final var actual = hospPersonConverter.convert(DR_KRANSTEGE_HOSP_CREDENTIALS);

      assertEquals(DR_KRANSTEGE_RESTRICTIONS, actual.getRestrictions());
    }

    @Test
    void shouldReturnEmptyListIfRestrictionsIsNull() {
      final var hospCredentials = kranstegeHospCredentialsBuilder().restrictions(null).build();

      final var actual = hospPersonConverter.convert(hospCredentials);

      assertTrue(actual.getRestrictions().isEmpty(),
          "Expected empty restrictions when RestrictionsDTO is null");
    }

    @Test
    void shouldThrowExceptionIfInvalidRestrictions() {
      final var hospCredentials = kranstegeHospCredentialsBuilder()
          .restrictions(
              List.of(
                  RestrictionDTO.builder()
                      .restrictionName("")
                      .restrictionCode(null)
                      .healthCareProfessionalLicenceCode("")
                      .build()
              )
          )
          .build();

      assertThrows(IllegalStateException.class, () -> hospPersonConverter.convert(hospCredentials));
    }
  }
}