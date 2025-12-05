package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_INFORMATION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;

@ExtendWith(MockitoExtension.class)
class GetHospInformationServiceTest {

  @Mock
  private HospRepository hospRepository;
  @InjectMocks
  private GetHospInformationService getHospInformationService;

  @Test
  void shouldThrowExceptionIfPersonIdIsNull() {
    final var request = GetHospInformationRequest.builder().build();
    assertThrows(IllegalArgumentException.class, () -> getHospInformationService.get(request));
  }

  @Test
  void shouldThrowExceptionIfPersonIdIsEmpty() {
    final var request = GetHospInformationRequest.builder()
        .personId("")
        .build();
    assertThrows(IllegalArgumentException.class, () -> getHospInformationService.get(request));
  }

  @Test
  void shouldThrowExceptionIfPersonIdIsBlank() {
    final var request = GetHospInformationRequest.builder()
        .personId("   ")
        .build();
    assertThrows(IllegalArgumentException.class, () -> getHospInformationService.get(request));
  }

  @Test
  void shouldReturnEmptyHospInformationIfNotFound() {
    final var expected = GetHospInformationResponse.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .build();

    final var request = GetHospInformationRequest.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .build();

    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(HospPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .build());

    final var actual = getHospInformationService.get(request);

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnHospInformationWhenFound() {
    final var request = GetHospInformationRequest.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .build();

    when(hospRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(
        DR_KRANSTEGE_HOSP_PERSON);

    final var actual = getHospInformationService.get(request);

    assertEquals(DR_KRANSTEGE_HOSP_INFORMATION, actual);
  }
}