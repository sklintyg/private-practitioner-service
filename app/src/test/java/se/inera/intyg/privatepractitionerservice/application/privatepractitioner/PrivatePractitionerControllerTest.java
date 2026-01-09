package se.inera.intyg.privatepractitionerservice.application.privatepractitioner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_INFORMATION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REGISTATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_UPDATE_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.REGISTER_CONFIGURATION_RESPONSE;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.CreateRegistrationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.EraseService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.GetHospInformationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.PrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.RegistrationConfigurationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.UpdatePrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.ValidatePrivatePractitionerService;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerControllerTest {

  private static final String PERSONAL_IDENTITY_NUMBER = "191212121212";

  @Mock
  private CreateRegistrationService createRegistrationService;
  @Mock
  private UpdatePrivatePractitionerService updatePrivatePractitionerService;
  @Mock
  private PrivatePractitionerService privatePractitionerService;
  @Mock
  private RegistrationConfigurationService registrationConfigurationService;
  @Mock
  private GetHospInformationService getHospInformationService;
  @Mock
  private ValidatePrivatePractitionerService validatePrivatePractitionerService;
  @Mock
  private EraseService eraseService;
  @InjectMocks
  private PrivatePractitionerController privatePractitionerController;

  @Test
  void shouldRegisterPrivatePractitioner() {
    when(createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST))
        .thenReturn(DR_KRANSTEGE_DTO);

    final var actual = privatePractitionerController.registerPrivatePractitioner(
        DR_KRANSTEGE_REGISTATION_REQUEST);

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(DR_KRANSTEGE_DTO, actual.getBody())
    );
  }

  @Test
  void shouldReturnConfiguration() {
    when(registrationConfigurationService.get()).thenReturn(REGISTER_CONFIGURATION_RESPONSE);

    final var actual = privatePractitionerController.getRegistrationConfiguration();

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(REGISTER_CONFIGURATION_RESPONSE, actual.getBody())
    );
  }

  @Test
  void shouldReturnHospInformation() {
    final var request = GetHospInformationRequest.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .build();

    when(getHospInformationService.get(request)).thenReturn(DR_KRANSTEGE_HOSP_INFORMATION);

    final var actual = privatePractitionerController.getHospInformation(request);

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(DR_KRANSTEGE_HOSP_INFORMATION, actual.getBody())
    );
  }

  @Test
  void shouldReturnPrivatePractitioners() {
    final var expected = List.of(
        PrivatePractitionerDTO.builder().hsaId("123").build(),
        PrivatePractitionerDTO.builder().hsaId("456").build()
    );

    when(privatePractitionerService.getPrivatePractitioners()).thenReturn(expected);

    final var actual = privatePractitionerController.getPrivatePractitioners();

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(expected, actual.getBody())
    );
  }

  @Test
  void shouldReturnPrivatePractitioner() {
    final var expected = PrivatePractitionerDTO.builder().hsaId("123").build();

    when(privatePractitionerService.getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER))
        .thenReturn(expected);

    final var actual = privatePractitionerController.getPrivatePractitioner(
        PERSONAL_IDENTITY_NUMBER);

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(expected, actual.getBody())
    );
  }

  @Test
  void shouldReturnPrivatePractitionerNotFound() {
    when(privatePractitionerService.getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER))
        .thenReturn(null);

    final var actual = privatePractitionerController.getPrivatePractitioner(
        PERSONAL_IDENTITY_NUMBER);

    assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
  }

  @Test
  void shouldValidatePrivatePractitioner() {
    when(validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(ValidatePrivatePractitionerResponse.builder().build());

    final var actual = privatePractitionerController.validatePrivatePractitioner(
        ValidatePrivatePractitionerRequest.builder()
            .personId(DR_KRANSTEGE_PERSON_ID)
            .build()
    );

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(ValidatePrivatePractitionerResponse.builder().build(), actual.getBody())
    );
  }

  @Test
  void shouldErasePrivatePractitioner() {
    privatePractitionerController.erasePrivatePractitioner(DR_KRANSTEGE_HSA_ID);

    verify(eraseService).erasePrivatePractitioner(DR_KRANSTEGE_HSA_ID);
  }

  @Test
  void shouldUpdatePrivatePractitioner() {
    when(updatePrivatePractitionerService.update(DR_KRANSTEGE_UPDATE_REQUEST)).thenReturn(
        DR_KRANSTEGE_DTO);

    final var actual = privatePractitionerController.updatePrivatePractitioner(
        DR_KRANSTEGE_UPDATE_REQUEST);

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(DR_KRANSTEGE_DTO, actual.getBody())
    );
  }
}
