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
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;

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

  @Mock
  private CreateRegistrationRequestValidator createRegistrationRequestValidator;
  @Mock
  private PrivatePractitionerFactory privatePractitionerFactory;
  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private HospRepository hospRepository;
  @Mock
  private NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  @Mock
  private PrivatePractitionerConverter privatePractitionerConverter;
  @InjectMocks
  private CreateRegistrationService createRegistrationService;

  @Test
  void shouldThrowIfValidatorThrows() {
    doThrow(new IllegalArgumentException())
        .when(createRegistrationRequestValidator).validate(DR_KRANSTEGE_REGISTATION_REQUEST);

    assertThrows(IllegalArgumentException.class,
        () -> createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST)
    );
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
        .thenReturn(HospPerson.builder()
            .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
            .build());

    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(privatePractitionerMock).updateWithHospInformation(any());
  }

  @Test
  void shouldAddPrivatePractitionerToCertifier() {
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST)).thenReturn(
        DR_KRANSTEGE);
    when(privatePractitionerRepository.save(DR_KRANSTEGE)).thenReturn(savePrivatePractitioner);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(
        HospPerson.builder()
            .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
            .build());
    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(hospRepository).addToCertifier(savePrivatePractitioner);
  }

  @Test
  void shouldNotifyPrivatePractitionerRegistered() {
    final var privatePractitionerMock = mock(PrivatePractitioner.class);
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    when(privatePractitionerMock.getPersonId()).thenReturn(DR_KRANSTEGE_PERSON_ID);
    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST)).thenReturn(
        privatePractitionerMock);
    when(privatePractitionerRepository.save(privatePractitionerMock)).thenReturn(
        savePrivatePractitioner);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(
        HospPerson.builder()
            .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
            .build());

    createRegistrationService.createRegistration(DR_KRANSTEGE_REGISTATION_REQUEST);

    verify(notifyPrivatePractitionerRegistration).notify(savePrivatePractitioner);
  }

  @Test
  void shouldReturnSavePrivatePractitioner() {
    final var savePrivatePractitioner = mock(PrivatePractitioner.class);

    when(privatePractitionerFactory.create(DR_KRANSTEGE_REGISTATION_REQUEST)).thenReturn(
        DR_KRANSTEGE);
    when(privatePractitionerRepository.save(DR_KRANSTEGE)).thenReturn(savePrivatePractitioner);
    when(privatePractitionerConverter.convert(savePrivatePractitioner))
        .thenReturn(DR_KRANSTEGE_DTO);
    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(
        HospPerson.builder()
            .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
            .build());

    final var actual = createRegistrationService.createRegistration(
        DR_KRANSTEGE_REGISTATION_REQUEST);

    assertEquals(DR_KRANSTEGE_DTO, actual);
  }
}