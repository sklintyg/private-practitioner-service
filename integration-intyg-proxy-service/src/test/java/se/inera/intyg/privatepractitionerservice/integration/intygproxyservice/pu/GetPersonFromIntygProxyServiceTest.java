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
  private static final String PERSON_NAME = "Frida Kranstege";

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

    assertEquals(PERSON_ID, actualResponse.getPerson().getPersonnummer());
  }

  @Test
  void shouldReturnPersonWithCorrectName() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = getPersonResponse();
    expectedResponse.getPerson().setNamn(PERSON_NAME);

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(PERSON_NAME, actualResponse.getPerson().getNamn());
  }

  @Test
  void shouldReturnStatusNotFoundWhenPersonDoesNotExist() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = PersonSvarDTO.builder()
        .status(StatusDTO.NOT_FOUND)
        .build();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
  }

  @Test
  void shouldReturnStatusErrorWhenServiceFails() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = PersonSvarDTO.builder()
        .status(StatusDTO.ERROR)
        .build();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
  }

  @Test
  void shouldReturnStatusFoundWhenPersonExists() {
    final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
    final var expectedResponse = PersonSvarDTO.builder()
        .status(StatusDTO.FOUND)
        .build();

    when(personFromIntygProxyServiceClient.get(personRequest)).thenReturn(expectedResponse);

    final var actualResponse = getPersonFromIntygProxyService.getPersonFromIntygProxy(
        personRequest);

    assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
  }


  private static PersonSvarDTO getPersonResponse() {
    return PersonSvarDTO.builder()
        .person(
            PersonDTO.builder()
                .personnummer(PERSON_ID)
                .build()
        )
        .status(StatusDTO.FOUND)
        .build();
  }
}