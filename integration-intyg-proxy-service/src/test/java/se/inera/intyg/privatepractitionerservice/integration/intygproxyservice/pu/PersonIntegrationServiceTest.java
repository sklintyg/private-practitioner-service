package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationResponse;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter.PersonSvarConverter;

@ExtendWith(MockitoExtension.class)
class PersonIntegrationServiceTest {

  @Mock
  private GetPersonFromIntygProxyService getPersonFromIntygProxyService;
  @Mock
  private PersonSvarConverter personSvarConverter;

  @InjectMocks
  private PersonIntegrationService personIntegrationService;

  private static final String PERSON_ID = "197705232382";
  public static final String KRANSTEGE_FIRST_NAME = "Frida";
  public static final String KRANSTEGE_LAST_NAME = "Kranstege";
  private static final String KRANSTEGE_FULL_NAME =
      KRANSTEGE_FIRST_NAME + " " + KRANSTEGE_LAST_NAME;

  @Nested
  class ErrorHandlingTest {

    @Test
    void shouldThrowIllegalArgumentExceptionIfPersonRequestIsNull() {
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(null)
      );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfPersonRequestContainsNullPersonId() {
      final var personRequest = new GetPersonIntegrationRequest(null);
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfPersonRequestContainsEmptyPersonId() {
      final var personRequest = new GetPersonIntegrationRequest("");
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldReturnStatusErrorIfCommunicationErrorWithIntygProxyOccurs() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenThrow(
          RuntimeException.class);
      assertThrows(RuntimeException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldThrowIllegalStateExceptionIfPersonNotFound() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      final var personSvarDTO = new PersonSvarDTO(null, StatusDTO.NOT_FOUND);

      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenReturn(
          personSvarDTO);

      assertThrows(IllegalStateException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldThrowIllegalStateExceptionIfErrorStatusReturnedFromPU() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      final var personSvarDTO = new PersonSvarDTO(null, StatusDTO.ERROR);

      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenReturn(
          personSvarDTO);

      assertThrows(IllegalStateException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }
  }

  @Nested
  class PersonResponseTest {

    @Test
    void shouldReturnPersonResponse() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      final var personSvarDTO = getPersonResponse();

      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenReturn(
          personSvarDTO);

      final var actual = personIntegrationService.getPerson(personRequest);

      assertEquals(GetPersonIntegrationResponse.class, actual.getClass());
    }

    @Test
    void shouldReturnPersonResponseWithConvertedStatus() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      final var personSvarDTO = getPersonResponse();
      final var expectedStatus = Status.FOUND;

      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenReturn(
          personSvarDTO);
      when(personSvarConverter.convertStatus(personSvarDTO.status())).thenReturn(expectedStatus);

      final var actual = personIntegrationService.getPerson(personRequest);

      assertEquals(expectedStatus, actual.getStatus());
    }

    @Test
    void shouldReturnPersonResponseWithConvertedPerson() {
      final var personRequest = new GetPersonIntegrationRequest(PERSON_ID);
      final var personSvarDTO = getPersonResponse();
      final var expectedPerson = getPerson();

      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenReturn(
          personSvarDTO);
      when(personSvarConverter.convertPerson(personSvarDTO.person())).thenReturn(expectedPerson);

      final var actual = personIntegrationService.getPerson(personRequest);

      assertEquals(expectedPerson, actual.getPerson());
    }
  }


  private static Person getPerson() {
    return Person.builder()
        .name(KRANSTEGE_FULL_NAME)
        .personId(PERSON_ID)
        .build();
  }

  private static PersonSvarDTO getPersonResponse() {
    return new PersonSvarDTO(getPersonDTO(), StatusDTO.FOUND);
  }

  private static PersonDTO getPersonDTO() {
    return new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, KRANSTEGE_LAST_NAME);
  }

}