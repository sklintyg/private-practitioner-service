package se.inera.intyg.privatepractitionerservice.integrationtest.privatepractitioner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.OK;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.addToCertifierResponseBuilder;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.fridaKranstegeCredentialsBuilder;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.fridaKranstegeHospCredentials;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.fridaKranstegePerson;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.fridaKranstegePersonWithSameName;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_CREDENTIALS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_INFORMATION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_INFORMATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REGISTATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_UPDATE_REQUEST;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonResponseDTO;
import se.inera.intyg.privatepractitionerservice.integrationtest.environment.Containers;
import se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock;
import se.inera.intyg.privatepractitionerservice.integrationtest.util.ApiUtil;
import se.inera.intyg.privatepractitionerservice.integrationtest.util.TestabilityApiUtil;

@ActiveProfiles({"integration-test", "testability"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PrivatePractitionerIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private ApiUtil api;
  private TestabilityApiUtil testabilityApi;
  private MockServerClient mockServerClient;
  private IntygProxyServiceMock intygProxyServiceMock;

  @BeforeAll
  static void beforeAll() {
    Containers.ensureRunning();
  }

  @BeforeEach
  void setUp() {
    this.api = new ApiUtil(restTemplate, port);
    this.testabilityApi = new TestabilityApiUtil(restTemplate, port);
    this.mockServerClient = new MockServerClient(
        Containers.mockServerContainer.getHost(),
        Containers.mockServerContainer.getServerPort()
    );
    this.intygProxyServiceMock = new IntygProxyServiceMock(mockServerClient);
  }

  @AfterEach
  void tearDown() throws Exception {
    testabilityApi.reset();
    mockServerClient.reset();
    Containers.redisContainer.execInContainer("redis-cli", "flushall");
  }

  @Test
  void shallRegisterPrivatePractitioner() {
    intygProxyServiceMock.credentialsForPersonResponse(
        fridaKranstegeCredentialsBuilder().build()
    );

    intygProxyServiceMock.certificationPersonResponse(
        addToCertifierResponseBuilder().build()
    );

    final var response = api.registerPrivatePractitioner(DR_KRANSTEGE_REGISTATION_REQUEST);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertAll(
        () -> assertEquals(DR_KRANSTEGE_DTO.getPersonId(), actual.getPersonId()),
        () -> assertEquals("SE165565594230-WEBCERT00001", actual.getHsaId()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getName(), actual.getName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getCareProviderName(), actual.getCareProviderName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getEmail(), actual.getEmail()),
        () -> assertNotNull(actual.getRegistrationDate())
    );
  }

  @Test
  void shallReturnHospInformation() {
    intygProxyServiceMock.credentialsForPersonResponse(
        fridaKranstegeCredentialsBuilder().build()
    );

    final var response = api.hospInformation(DR_KRANSTEGE_HOSP_INFORMATION_REQUEST);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertEquals(DR_KRANSTEGE_HOSP_INFORMATION, actual);
  }

  @Test
  void shallReturnRegistrationConfiguration() {
    final var response = api.registrationConfiguration();

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertAll(
        () -> assertNotNull(actual.getHealthcareServiceTypeCodes(),
            "HealthcareServiceType codes should not be null"),
        () -> assertFalse(actual.getHealthcareServiceTypeCodes().isEmpty(),
            "HealthcareServiceType codes should not be empty"),
        () -> assertNotNull(actual.getPositionCodes(), "Position codes should not be null"),
        () -> assertFalse(actual.getPositionCodes().isEmpty(),
            "Position codes should not be empty"),
        () -> assertNotNull(actual.getTypeOfCareCodes(), "TypeOfCare codes should not be null"),
        () -> assertFalse(actual.getTypeOfCareCodes().isEmpty(),
            "TypeOfCare codes should not be empty")
    );
  }

  @Test
  void shallUpdatePrivatePractitioner() {
    intygProxyServiceMock.credentialsForPersonResponse(
        fridaKranstegeCredentialsBuilder().build()
    );

    intygProxyServiceMock.certificationPersonResponse(
        addToCertifierResponseBuilder().build()
    );

    testabilityApi.addPrivatePractitioner(DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST);

    final var response = api.updatePrivatePractitioner(DR_KRANSTEGE_UPDATE_REQUEST);
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertAll(
        () -> assertEquals(DR_KRANSTEGE_DTO.getPersonId(), actual.getPersonId()),
        () -> assertEquals("SE165565594230-WEBCERT00001", actual.getHsaId()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getName(), actual.getName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getCareProviderName(), actual.getCareProviderName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getEmail(), actual.getEmail()),
        () -> assertNotNull(actual.getRegistrationDate())
    );
  }

  @Test
  void shallUpdateNameFromPUWhenNameHasChanged() {
    intygProxyServiceMock.credentialsForPersonResponse(
        fridaKranstegeCredentialsBuilder().build()
    );

    intygProxyServiceMock.certificationPersonResponse(
        addToCertifierResponseBuilder().build()
    );

    testabilityApi.addPrivatePractitioner(DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST);

    intygProxyServiceMock.personResponse(fridaKranstegePerson());

    final var response = api.getPrivatePractitioner(DR_KRANSTEGE_PERSON_ID);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertEquals("Frida Andersson", actual.getName());
  }

  @Test
  void shallNotUpdateNameFromPUWhenNameHasNotChanged() {
    intygProxyServiceMock.credentialsForPersonResponse(
        fridaKranstegeCredentialsBuilder().build()
    );

    intygProxyServiceMock.certificationPersonResponse(
        addToCertifierResponseBuilder().build()
    );

    testabilityApi.addPrivatePractitioner(DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST);

    intygProxyServiceMock.personResponse(
        fridaKranstegePersonWithSameName()
    );

    final var response = api.getPrivatePractitioner(DR_KRANSTEGE_PERSON_ID);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertEquals("Frida Kranstege", actual.getName());
  }

  @Test
  void shallRestrictPrivatePractitionerWithRevokedLicense() {

    testabilityApi.addPrivatePractitioner(
        DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST);

    intygProxyServiceMock.credentialsForPersonResponse(
        GetCredentialsForPersonResponseDTO.builder()
            .credentials(
                fridaKranstegeHospCredentials()
                    .restrictions(DR_KRANSTEGE_HOSP_CREDENTIALS.getRestrictions())
                    .build()
            )
            .build()
    );

    intygProxyServiceMock.lastUpdate();

    final var response = api.validatePrivatePractitioner(
        ValidatePrivatePractitionerRequest.builder()
            .personId(DR_KRANSTEGE_PERSON_ID)
            .build());
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertEquals(NOT_AUTHORIZED_IN_HOSP, actual.getResultCode());
  }

  @Test
  void shallNotRestrictPrivatePractitionerWithValidLicense() {

    testabilityApi.addPrivatePractitioner(
        DR_KRANSTEGE_TESTABILITY_REGISTATION_REQUEST);

    intygProxyServiceMock.credentialsForPersonResponse(
        GetCredentialsForPersonResponseDTO.builder()
            .credentials(
                fridaKranstegeHospCredentials()
                    .build()
            )
            .build()
    );

    intygProxyServiceMock.lastUpdate();

    final var response = api.validatePrivatePractitioner(
        ValidatePrivatePractitionerRequest.builder()
            .personId(DR_KRANSTEGE_PERSON_ID)
            .build());
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertEquals(OK, actual.getResultCode());
  }
}
