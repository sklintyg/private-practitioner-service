package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.PersonFromIntygProxyServiceClient;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@ExtendWith(MockitoExtension.class)
class GetPersonFromIntygProxyServiceTest {

  @Mock
  PersonFromIntygProxyServiceClient personFromIntygProxyServiceClient;
  @InjectMocks
  GetPersonFromIntygProxyService getPersonFromIntygProxyService;

  private static final String PERSON_ID = "197705232382";
  private static final String KRANSTEGE_NAME = "Frida";
  public static final String KRANSTEGE_LAST_NAME = "Kranstege";

  @Test
  void shouldReturnPersonFromIntygProxyService() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = getPersonResponse();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(PersonSvarDTO.class, actualResponse.getClass());
  }

  @Test
  void shouldReturnPersonWithCorrectPersonId() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = getPersonResponse();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(PERSON_ID, actualResponse.person().personnummer());
  }

  @Test
  void shouldReturnPersonWithCorrectName() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = getPersonResponse();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(KRANSTEGE_NAME, actualResponse.person().fornamn());
  }

  @Test
  void shouldReturnPersonWithCorrectLastName() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = getPersonResponse();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(KRANSTEGE_LAST_NAME, actualResponse.person().efternamn());
  }

  @Test
  void shouldReturnStatusNotFoundWhenPersonDoesNotExist() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = new PersonSvarDTO(null, StatusDTO.NOT_FOUND);

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.status(), actualResponse.status());
  }

  @Test
  void shouldReturnStatusErrorWhenServiceFails() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = new PersonSvarDTO(null, StatusDTO.ERROR);

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.status(), actualResponse.status());
  }

  @Test
  void shouldReturnStatusFoundWhenPersonExists() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = new PersonSvarDTO(null, StatusDTO.FOUND);

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.status(), actualResponse.status());
  }


  private static PersonSvarDTO getPersonResponse() {
    return new PersonSvarDTO(new PersonDTO(PERSON_ID, KRANSTEGE_NAME, KRANSTEGE_LAST_NAME), null);
  }
}