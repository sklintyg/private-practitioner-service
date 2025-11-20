package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationResponse;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;

@ExtendWith(MockitoExtension.class)
class UpdatePrivatePractitionerFromPUServiceTest {

  @Mock
  private GetPersonIntegrationService getPersonIntegrationService;

  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;

  @InjectMocks
  private UpdatePrivatePractitionerFromPUService updatePrivatePractitionerFromPUService;

  private PrivatePractitioner practitioner;

  @BeforeEach
  void setUp() {
    practitioner = PrivatePractitioner.builder()
        .personId("197705232382")
        .name("Frida Kranstege")
        .hsaId("HSAID")
        .build();
  }

  @Test
  void shouldReturnPractitionerWhenStatusIsNotFound() {
    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.NOT_FOUND)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldReturnPractitionerWhenStatusIsError() {
    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.ERROR)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldReturnPractitionerWhenNameHasNotChanged() {
    final var puPerson = Person.builder()
        .personId("197705232382")
        .name("Frida Kranstege")
        .build();

    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.FOUND)
        .person(puPerson)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldUpdateNameWhenNameHasChanged() {
    final var puPerson = Person.builder()
        .personId("197705232382")
        .name("Frida Andersson")
        .build();

    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.FOUND)
        .person(puPerson)
        .build();

    final var updatedPractitioner = PrivatePractitioner.builder()
        .personId("197705232382")
        .name("Frida Andersson")
        .hsaId("HSAID")
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);
    when(privatePractitionerRepository.save(any(PrivatePractitioner.class)))
        .thenReturn(updatedPractitioner);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals("Frida Andersson", result.getName());
  }

  @Test
  void shouldSavePractitionerWhenNameHasChanged() {
    final var puPerson = Person.builder()
        .personId("197705232382")
        .name("Frida Johansson")
        .build();

    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.FOUND)
        .person(puPerson)
        .build();

    final var updatedPractitioner = PrivatePractitioner.builder()
        .personId("197705232382")
        .name("Frida Johansson")
        .hsaId("HSAID")
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);
    when(privatePractitionerRepository.save(any(PrivatePractitioner.class)))
        .thenReturn(updatedPractitioner);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(privatePractitionerRepository).save(any(PrivatePractitioner.class));
  }

  @Test
  void shouldNotSavePractitionerWhenNameHasNotChanged() {
    final var puPerson = Person.builder()
        .personId("197705232382")
        .name("Frida Kranstege")
        .build();

    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.FOUND)
        .person(puPerson)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(privatePractitionerRepository, never()).save(any(PrivatePractitioner.class));
  }

  @Test
  void shouldNotSavePractitionerWhenStatusIsNotFound() {
    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.NOT_FOUND)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(privatePractitionerRepository, never()).save(any(PrivatePractitioner.class));
  }

  @Test
  void shouldCallGetPersonIntegrationServiceWithCorrectPersonId() {
    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.NOT_FOUND)
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(getPersonIntegrationService).getPerson(any(GetPersonIntegrationRequest.class));
  }

  @Test
  void shouldReturnUpdatedPractitionerFromRepository() {
    final var puPerson = Person.builder()
        .personId("197705232382")
        .name("Frida Lindberg")
        .build();

    final var response = GetPersonIntegrationResponse.builder()
        .status(Status.FOUND)
        .person(puPerson)
        .build();

    final var updatedPractitioner = PrivatePractitioner.builder()
        .personId("197705232382")
        .name("Frida Lindberg")
        .hsaId("HSAID")
        .build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);
    when(privatePractitionerRepository.save(any(PrivatePractitioner.class)))
        .thenReturn(updatedPractitioner);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(updatedPractitioner, result);
  }
}

