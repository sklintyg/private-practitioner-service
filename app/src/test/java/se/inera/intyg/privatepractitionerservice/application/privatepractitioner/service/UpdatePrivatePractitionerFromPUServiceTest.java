/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationRequest;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationResponse;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.GetPersonIntegrationService;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;

@ExtendWith(MockitoExtension.class)
class UpdatePrivatePractitionerFromPUServiceTest {

  @Mock private GetPersonIntegrationService getPersonIntegrationService;

  @Mock private PrivatePractitionerRepository privatePractitionerRepository;

  @Mock private HashUtility hashUtility;

  @InjectMocks
  private UpdatePrivatePractitionerFromPUService updatePrivatePractitionerFromPUService;

  private PrivatePractitioner practitioner;

  @BeforeEach
  void setUp() {
    practitioner =
        PrivatePractitioner.builder()
            .personId("197705232382")
            .name("Frida Kranstege")
            .hsaId("HSAID")
            .build();
  }

  @Test
  void shouldReturnPractitionerWhenStatusIsNotFound() {
    final var response = GetPersonIntegrationResponse.builder().status(Status.NOT_FOUND).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldReturnPractitionerWhenStatusIsError() {
    final var response = GetPersonIntegrationResponse.builder().status(Status.ERROR).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldReturnPractitionerWhenNameHasNotChanged() {
    final var puPerson = Person.builder().personId("197705232382").name("Frida Kranstege").build();

    final var response =
        GetPersonIntegrationResponse.builder().status(Status.FOUND).person(puPerson).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    final var result = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, result);
  }

  @Test
  void shouldUpdateNameWhenNameHasChanged() {
    final var puPerson = Person.builder().personId("197705232382").name("Frida Andersson").build();

    final var response =
        GetPersonIntegrationResponse.builder().status(Status.FOUND).person(puPerson).build();

    final var updatedPractitioner =
        PrivatePractitioner.builder()
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
    final var puPerson = Person.builder().personId("197705232382").name("Frida Johansson").build();

    final var response =
        GetPersonIntegrationResponse.builder().status(Status.FOUND).person(puPerson).build();

    final var updatedPractitioner =
        PrivatePractitioner.builder()
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
    final var puPerson = Person.builder().personId("197705232382").name("Frida Kranstege").build();

    final var response =
        GetPersonIntegrationResponse.builder().status(Status.FOUND).person(puPerson).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(privatePractitionerRepository, never()).save(any(PrivatePractitioner.class));
  }

  @Test
  void shouldNotSavePractitionerWhenStatusIsNotFound() {
    final var response = GetPersonIntegrationResponse.builder().status(Status.NOT_FOUND).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(privatePractitionerRepository, never()).save(any(PrivatePractitioner.class));
  }

  @Test
  void shouldCallGetPersonIntegrationServiceWithCorrectPersonId() {
    final var response = GetPersonIntegrationResponse.builder().status(Status.NOT_FOUND).build();

    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenReturn(response);

    updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    verify(getPersonIntegrationService).getPerson(any(GetPersonIntegrationRequest.class));
  }

  @Test
  void shouldReturnUpdatedPractitionerFromRepository() {
    final var puPerson = Person.builder().personId("197705232382").name("Frida Lindberg").build();

    final var response =
        GetPersonIntegrationResponse.builder().status(Status.FOUND).person(puPerson).build();

    final var updatedPractitioner =
        PrivatePractitioner.builder()
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

  @Test
  void shouldReturnPractitionerWhenGetPersonIntegrationServiceThrows() {
    when(getPersonIntegrationService.getPerson(any(GetPersonIntegrationRequest.class)))
        .thenThrow(IllegalStateException.class);

    final var actual = updatePrivatePractitionerFromPUService.updateFromPu(practitioner);

    assertEquals(practitioner, actual);
  }
}
