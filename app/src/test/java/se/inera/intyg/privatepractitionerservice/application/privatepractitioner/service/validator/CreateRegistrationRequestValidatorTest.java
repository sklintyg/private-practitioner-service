package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_POSITION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REGISTATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.kranstegeRegistrationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class CreateRegistrationRequestValidatorTest {

  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private CodeSystemRepository codeSystemRepository;
  @InjectMocks
  private CreateRegistrationRequestValidator createRegistrationRequestValidator;

  @BeforeEach
  void setUp() {
    lenient().when(codeSystemRepository.positionExists(DR_KRANSTEGE_POSITION)).thenReturn(true);
    lenient().when(codeSystemRepository.typeOfCareExists(DR_KRANSTEGE_TYPE_OF_CARE))
        .thenReturn(true);
    lenient().when(
            codeSystemRepository.healthcareServiceTypeExists(DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE)
        )
        .thenReturn(true);
  }

  @Test
  void shouldNotThrowIfValidRequest() {
    createRegistrationRequestValidator.validate(DR_KRANSTEGE_REGISTATION_REQUEST);
  }

  @Test
  void shouldThrowExceptionIfPersonIdNull() {
    final var request = kranstegeRegistrationRequest()
        .personId(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("PersonId is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPersonIdBlank() {
    final var request = kranstegeRegistrationRequest()
        .personId("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("PersonId is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfNameNull() {
    final var request = kranstegeRegistrationRequest()
        .name(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Name is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfNameBlank() {
    final var request = kranstegeRegistrationRequest()
        .name("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Name is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionNull() {
    final var request = kranstegeRegistrationRequest()
        .position(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Position is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionBlank() {
    final var request = kranstegeRegistrationRequest()
        .position("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Position is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPositionDontExist() {
    final var request = kranstegeRegistrationRequest()
        .position("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Position 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCareUnitNameNull() {
    final var request = kranstegeRegistrationRequest()
        .careUnitName(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("CareUnitName is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCareUnitNameBlank() {
    final var request = kranstegeRegistrationRequest()
        .careUnitName("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("CareUnitName is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareNull() {
    final var request = kranstegeRegistrationRequest()
        .typeOfCare(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareBlank() {
    final var request = kranstegeRegistrationRequest()
        .typeOfCare("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfTypeOfCareDontExist() {
    final var request = kranstegeRegistrationRequest()
        .typeOfCare("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("TypeOfCare 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeNull() {
    final var request = kranstegeRegistrationRequest()
        .healthcareServiceType(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeBlank() {
    final var request = kranstegeRegistrationRequest()
        .healthcareServiceType("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfHealthcareServiceTypeDontExist() {
    final var request = kranstegeRegistrationRequest()
        .healthcareServiceType("xx")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("HealthcareServiceType 'xx' is invalid", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPhoneNumberNull() {
    final var request = kranstegeRegistrationRequest()
        .phoneNumber(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("PhoneNumber is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfPhoneNumberBlank() {
    final var request = kranstegeRegistrationRequest()
        .phoneNumber("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("PhoneNumber is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfEmailNull() {
    final var request = kranstegeRegistrationRequest()
        .email(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Email is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfEmailBlank() {
    final var request = kranstegeRegistrationRequest()
        .email("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Email is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfAddressNull() {
    final var request = kranstegeRegistrationRequest()
        .address(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Address is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfAddressBlank() {
    final var request = kranstegeRegistrationRequest()
        .address("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Address is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfZipCodeNull() {
    final var request = kranstegeRegistrationRequest()
        .zipCode(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("ZipCode is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfZipCodeBlank() {
    final var request = kranstegeRegistrationRequest()
        .zipCode("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("ZipCode is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCityNull() {
    final var request = kranstegeRegistrationRequest()
        .city(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("City is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCityBlank() {
    final var request = kranstegeRegistrationRequest()
        .city("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("City is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfMunicipalityNull() {
    final var request = kranstegeRegistrationRequest()
        .municipality(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Municipality is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfMunicipalityBlank() {
    final var request = kranstegeRegistrationRequest()
        .municipality("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("Municipality is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCountyNull() {
    final var request = kranstegeRegistrationRequest()
        .county(null)
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("County is required", actual.getMessage());
  }

  @Test
  void shouldThrowExceptionIfCountyBlank() {
    final var request = kranstegeRegistrationRequest()
        .county("   ")
        .build();

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(request)
    );

    assertEquals("County is required", actual.getMessage());
  }

  @Test
  void shouldThrowIfRegistrationAlreadyExists() {
    when(privatePractitionerRepository.isExists(DR_KRANSTEGE_REGISTATION_REQUEST.getPersonId()))
        .thenReturn(true);

    final var actual = assertThrows(
        PrivatlakarportalServiceException.class,
        () -> createRegistrationRequestValidator.validate(DR_KRANSTEGE_REGISTATION_REQUEST)
    );

    assertEquals("Registration already exists", actual.getMessage());
  }
}