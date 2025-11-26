package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_CREDENTIALS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeHospPersonBuilder;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@ExtendWith(MockitoExtension.class)
class HospRepositoryTest {

  @Mock
  private HospService hospService;
  @Mock
  private HashUtility hashUtility;
  @InjectMocks
  private HospRepository hospRepository;

  @Test
  void shouldAddToCertifier() {
    when(hospService.handleHospCertificationPersonResponseType(
        anyString(), anyString(), anyString(), any())
    ).thenReturn(
        Result.builder()
            .resultCode("0")
            .resultText("Success")
            .build()
    );

    hospRepository.addToCertifier(DR_KRANSTEGE);

    verify(hospService).handleHospCertificationPersonResponseType(
        DR_KRANSTEGE.getHsaId(),
        "add",
        DR_KRANSTEGE.getPersonId(),
        null
    );
  }

  @Test
  void shouldReturnEmptyWhenNoHospPersonFound() {
    when(hospService.getHospCredentialsForPersonResponseType(DR_KRANSTEGE.getHsaId()))
        .thenReturn(null);
    final var actual = hospRepository.findByPersonId(DR_KRANSTEGE.getHsaId());
    assertTrue(actual.isEmpty());
  }

  @Test
  void shouldReturnWhenHospPersonFound() {
    when(hospService.getHospCredentialsForPersonResponseType(DR_KRANSTEGE.getHsaId()))
        .thenReturn(DR_KRANSTEGE_HOSP_CREDENTIALS);

    final var actual = hospRepository.findByPersonId(DR_KRANSTEGE.getHsaId());

    assertEquals(DR_KRANSTEGE_HOSP_PERSON, actual.orElseThrow());
  }

  @Test
  void shouldReturnHospInformationIfHospUpdated() {
    final var expected = kranstegeHospPersonBuilder()
        .hospUpdated(LocalDateTime.now())
        .build();

    final var drKranstege = kranstegeBuilder()
        .hospUpdated(LocalDateTime.now().minusDays(1))
        .build();

    when(hospService.getHospLastUpdate()).thenReturn(expected.getHospUpdated());
    when(hospService.getHospCredentialsForPersonResponseType(drKranstege.getPersonId()))
        .thenReturn(DR_KRANSTEGE_HOSP_CREDENTIALS);

    final var actual = hospRepository.updatedHospPerson(drKranstege);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldReturnEmptyHospInformationIfUserRemovedFromHosp() {
    final var expected = HospPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .hospUpdated(LocalDateTime.now())
        .build();

    final var drKranstege = kranstegeBuilder()
        .hospUpdated(LocalDateTime.now().minusDays(1))
        .build();

    when(hospService.getHospLastUpdate()).thenReturn(expected.getHospUpdated());
    when(hospService.getHospCredentialsForPersonResponseType(drKranstege.getPersonId()))
        .thenReturn(HospCredentialsForPerson.builder().build());

    final var actual = hospRepository.updatedHospPerson(drKranstege);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldEmptyHospInformationIfNotHospUpdated() {
    final var drKranstege = kranstegeBuilder()
        .hospUpdated(LocalDateTime.now().plusDays(1))
        .build();

    when(hospService.getHospLastUpdate()).thenReturn(LocalDateTime.now());

    final var actual = hospRepository.updatedHospPerson(drKranstege);

    assertTrue(actual.isEmpty(), "Expected empty when no hosp update is needed");
  }


}