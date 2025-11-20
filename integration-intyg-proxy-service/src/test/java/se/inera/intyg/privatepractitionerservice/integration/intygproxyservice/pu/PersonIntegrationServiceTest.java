package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.GetPersonFromIntygProxyService;
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
  private static final String PERSON_NAME = "Frida Kranstege";

  @Nested
  class ErrorHandlingTest {

    @Test
    void shouldThrowIlligalArgumentExceptionIfPersonRequestIsNull() {
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(null)
      );
    }

    @Test
    void shouldThrowIlligalArgumentExceptionIfPersonRequestContainsNullPersonId() {
      final var personRequest = GetPersonIntegrationRequest.builder().personId(null).build();
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldThrowIlligalArgumentExceptionIfPersonRequestContainsEmptyPersonId() {
      final var personRequest = GetPersonIntegrationRequest.builder().personId("").build();
      assertThrows(IllegalArgumentException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }

    @Test
    void shouldReturnStatusErrorIfCommunicationErrorWithIntygProxyOccurs() {
      final var personRequest = GetPersonIntegrationRequest.builder().personId(PERSON_ID).build();
      when(getPersonFromIntygProxyService.getPersonFromIntygProxy(personRequest)).thenThrow(
          RuntimeException.class);
      assertThrows(RuntimeException.class,
          () -> personIntegrationService.getPerson(personRequest)
      );
    }
  }


  private static Person getPerson() {
    return Person.builder()
        .name(PERSON_NAME)
        .personId(PERSON_ID)
        .build();
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