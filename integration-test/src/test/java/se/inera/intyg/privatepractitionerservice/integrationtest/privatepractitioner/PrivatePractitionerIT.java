package se.inera.intyg.privatepractitionerservice.integrationtest.privatepractitioner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.addToCertifierResponseBuilder;
import static se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock.fridaKranstegeCredentialsBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REQUEST;

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
import se.inera.intyg.privatepractitionerservice.integrationtest.environment.Containers;
import se.inera.intyg.privatepractitionerservice.integrationtest.environment.IntygProxyServiceMock;
import se.inera.intyg.privatepractitionerservice.integrationtest.util.ApiUtil;

@ActiveProfiles({"integration-test", "testability"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PrivatePractitionerIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private ApiUtil api;
  private MockServerClient mockServerClient;
  private IntygProxyServiceMock intygProxyServiceMock;

  @BeforeAll
  static void beforeAll() {
    Containers.ensureRunning();
  }

  @BeforeEach
  void setUp() {
    this.api = new ApiUtil(restTemplate, port);
    this.mockServerClient = new MockServerClient(
        Containers.mockServerContainer.getHost(),
        Containers.mockServerContainer.getServerPort()
    );
    this.intygProxyServiceMock = new IntygProxyServiceMock(mockServerClient);
  }

  @AfterEach
  void tearDown() throws Exception {
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

    final var response = api.registerPrivatePractitioner(DR_KRANSTEGE_REQUEST);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertAll(
        () -> assertEquals(DR_KRANSTEGE_DTO.getPersonId(), actual.getPersonId()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getHsaId(), actual.getHsaId()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getName(), actual.getName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getCareProviderName(), actual.getCareProviderName()),
        () -> assertEquals(DR_KRANSTEGE_DTO.getEmail(), actual.getEmail()),
        () -> assertNotNull(actual.getRegistrationDate())
    );
  }

  @Test
  void shallReturnRegistrationConfiguration() {
    final var response = api.registrationConfiguration();

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    final var actual = response.getBody();
    assertAll(
        () -> assertNotNull(actual.getConsentForm(), "Consent form should not be null"),
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
}
