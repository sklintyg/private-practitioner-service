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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_REGISTATION_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerFactory;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.CreateRegistrationRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class CreateRegistrationServiceTest {

  @Mock private CreateRegistrationRequestValidator createRegistrationRequestValidator;
  @Mock private PrivatePractitionerFactory privatePractitionerFactory;
  @Mock private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock private HospRepository hospRepository;
  @Mock private NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  @Mock private PrivatePractitionerConverter privatePractitionerConverter;
  @InjectMocks private CreateRegistrationService createRegistrationService;

  @Test
  void shouldThrowIfValidatorThrows() {
    doThrow(new IllegalArgumentException())
        .when(createRegistrationRequestValidator)
        .validate(DR_KRANSTEGE_REGISTATION_REQUEST);

    assertThrows(
        IllegalArgumentException.class,
        () -> createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST));
  }

  @Test
  void shouldUpdatePrivatePractitionerIfHospPresent() {
    final var privatePractitionerMock = mock(PrivatePractitioner.class);

    when(privatePractitionerMock.getPersonId()).thenReturn(DR_KRANSTEGE_PERSON_ID);

    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST))
        .thenReturn(privatePractitionerMock);

    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(DR_KRANSTEGE_HOSP_PERSON);

    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(privatePractitionerMock).updateWithHospInformation(DR_KRANSTEGE_HOSP_PERSON);
  }

  @Test
  void shouldUpdatePrivatePractitionerWithHospDateIfHospMissing() {
    final var privatePractitionerMock = mock(PrivatePractitioner.class);

    when(privatePractitionerMock.getPersonId()).thenReturn(DR_KRANSTEGE_PERSON_ID);

    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST))
        .thenReturn(privatePractitionerMock);

    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(HospPerson.builder().personalIdentityNumber(DR_KRANSTEGE_PERSON_ID).build());

    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(privatePractitionerMock).updateWithHospInformation(any());
  }

  @Test
  void shouldAddPrivatePractitionerToCertifier() {
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    final var kranstege = kranstegeBuilder().build();
    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST)).thenReturn(kranstege);
    when(privatePractitionerRepository.save(kranstege)).thenReturn(savePrivatePractitioner);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(HospPerson.builder().personalIdentityNumber(DR_KRANSTEGE_PERSON_ID).build());
    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(hospRepository).addToCertifier(savePrivatePractitioner);
  }

  @Test
  void shouldNotifyPrivatePractitionerRegistered() {
    final var privatePractitionerMock = mock(PrivatePractitioner.class);
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    when(privatePractitionerMock.getPersonId()).thenReturn(DR_KRANSTEGE_PERSON_ID);
    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST))
        .thenReturn(privatePractitionerMock);
    when(privatePractitionerRepository.save(privatePractitionerMock))
        .thenReturn(savePrivatePractitioner);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(HospPerson.builder().personalIdentityNumber(DR_KRANSTEGE_PERSON_ID).build());

    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(notifyPrivatePractitionerRegistration).notify(savePrivatePractitioner);
  }

  @Test
  void shouldReturnSavePrivatePractitioner() {
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    final var kranstege = kranstegeBuilder().build();
    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST)).thenReturn(kranstege);
    when(privatePractitionerRepository.save(kranstege)).thenReturn(savePrivatePractitioner);
    when(privatePractitionerConverter.convert(savePrivatePractitioner))
        .thenReturn(DR_KRANSTEGE_DTO);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(HospPerson.builder().personalIdentityNumber(DR_KRANSTEGE_PERSON_ID).build());

    final var actual =
        createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    assertEquals(DR_KRANSTEGE_DTO, actual);
  }
}
