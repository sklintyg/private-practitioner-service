package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_POSITION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_UPDATE_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.kranstegeUpdateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;

@ExtendWith(MockitoExtension.class)
class UpdatePrivatePractitionerRequestValidatorTest {

  @Mock
  private CodeSystemRepository codeSystemRepository;

  private UpdatePrivatePractitionerRequestValidator updatePrivatePractitionerRequestValidator;

  @BeforeEach
  void setUp() {
    lenient().when(codeSystemRepository.positionExists(DR_KRANSTEGE_POSITION)).thenReturn(true);
    lenient().when(codeSystemRepository.typeOfCareExists(DR_KRANSTEGE_TYPE_OF_CARE))
        .thenReturn(true);
    lenient().when(
            codeSystemRepository.healthcareServiceTypeExists(DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE)
        )
        .thenReturn(true);

    PrivatePractitionerValidationHelper validationHelper = new PrivatePractitionerValidationHelper(
        codeSystemRepository);
    updatePrivatePractitionerRequestValidator = new UpdatePrivatePractitionerRequestValidator(
        validationHelper);
  }

  @Test
  void shouldNotThrowIfValidRequest() {
    updatePrivatePractitionerRequestValidator.validate(DR_KRANSTEGE_UPDATE_REQUEST);
  }

  @Test
  void shouldThrowExceptionIfPersonIdNull() {
    final var request = kranstegeUpdateRequest()
        .personId(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("PersonId is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPersonIdBlank() {
    final var request = kranstegeUpdateRequest()
        .personId("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("PersonId is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfNameNull() {
    final var request = kranstegeUpdateRequest()
        .name(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Name is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfNameBlank() {
    final var request = kranstegeUpdateRequest()
        .name("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Name is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionNull() {
    final var request = kranstegeUpdateRequest()
        .position(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Position is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionBlank() {
    final var request = kranstegeUpdateRequest()
        .position("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Position is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionDontExist() {
    final var request = kranstegeUpdateRequest()
        .position("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Position 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCareUnitNameNull() {
    final var request = kranstegeUpdateRequest()
        .careUnitName(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("CareUnitName is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCareUnitNameBlank() {
    final var request = kranstegeUpdateRequest()
        .careUnitName("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("CareUnitName is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareNull() {
    final var request = kranstegeUpdateRequest()
        .typeOfCare(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareBlank() {
    final var request = kranstegeUpdateRequest()
        .typeOfCare("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareDontExist() {
    final var request = kranstegeUpdateRequest()
        .typeOfCare("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeNull() {
    final var request = kranstegeUpdateRequest()
        .healthcareServiceType(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeBlank() {
    final var request = kranstegeUpdateRequest()
        .healthcareServiceType("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeDontExist() {
    final var request = kranstegeUpdateRequest()
        .healthcareServiceType("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPhoneNumberNull() {
    final var request = kranstegeUpdateRequest()
        .phoneNumber(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("PhoneNumber is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPhoneNumberBlank() {
    final var request = kranstegeUpdateRequest()
        .phoneNumber("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("PhoneNumber is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfEmailNull() {
    final var request = kranstegeUpdateRequest()
        .email(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Email is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfEmailBlank() {
    final var request = kranstegeUpdateRequest()
        .email("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Email is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfAddressNull() {
    final var request = kranstegeUpdateRequest()
        .address(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Address is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfAddressBlank() {
    final var request = kranstegeUpdateRequest()
        .address("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Address is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfZipCodeNull() {
    final var request = kranstegeUpdateRequest()
        .zipCode(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("ZipCode is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfZipCodeBlank() {
    final var request = kranstegeUpdateRequest()
        .zipCode("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("ZipCode is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCityNull() {
    final var request = kranstegeUpdateRequest()
        .city(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("City is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCityBlank() {
    final var request = kranstegeUpdateRequest()
        .city("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("City is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfMunicipalityNull() {
    final var request = kranstegeUpdateRequest()
        .municipality(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Municipality is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfMunicipalityBlank() {
    final var request = kranstegeUpdateRequest()
        .municipality("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("Municipality is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCountyNull() {
    final var request = kranstegeUpdateRequest()
        .county(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("County is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCountyBlank() {
    final var request = kranstegeUpdateRequest()
        .county("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> updatePrivatePractitionerRequestValidator.validate(request)
    );

    assertEquals("County is required", actual.getMessage());
  }
}