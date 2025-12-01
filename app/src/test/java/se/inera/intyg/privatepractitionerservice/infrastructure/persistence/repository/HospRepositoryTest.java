package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_HOSP_CREDENTIALS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeHospPersonBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.HospUppdateringEntity;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@ExtendWith(MockitoExtension.class)
class HospRepositoryTest {

  @Mock
  private HospService hospService;
  @Mock
  private HashUtility hashUtility;
  @Mock
  private HospUppdateringEntityRepository hospUppdateringEntityRepository;
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
  void shouldEmptyHospInformationIfNotHospUpdated() {
    final var drKranstege = kranstegeBuilder()
        .hospUpdated(LocalDateTime.now().plusDays(1))
        .build();

    when(hospService.getHospLastUpdate()).thenReturn(LocalDateTime.now());

    final var actual = hospRepository.updatedHospPerson(drKranstege);

    assertTrue(actual.isEmpty(), "Expected empty when no hosp update is needed");
  }

  @Test
  void shouldReturnTrueWhenHospUpdateIsEmpty() {
    when(hospService.getHospLastUpdate()).thenReturn(LocalDateTime.now());
    when(hospUppdateringEntityRepository.findHospUppdatering()).thenReturn(Optional.empty());

    final var actual = hospRepository.needUpdateFromHosp();
    assertTrue(actual, "Expected true when stored hosp update is empty");
  }

  @Test
  void shouldReturnFalseWhenLastHospUpdateIsNull() {
    when(hospService.getHospLastUpdate()).thenReturn(null);

    final var actual = hospRepository.needUpdateFromHosp();
    assertFalse(actual, "Expected false when hosp last update is null");
  }

  @Test
  void shouldReturnTrueWhenHospLastUpdateIsAfterHospUpdate() {
    when(hospService.getHospLastUpdate()).thenReturn(LocalDateTime.now());
    when(hospUppdateringEntityRepository.findHospUppdatering()).thenReturn(
        Optional.of(new HospUppdateringEntity(LocalDateTime.now().minusDays(1)))
    );

    final var actual = hospRepository.needUpdateFromHosp();
    assertTrue(actual, "Expected true when hosp last update is after stored hosp update");
  }

  @Test
  void shouldReturnFalseWhenHospLastUpdateIsBeforeHospUpdate() {
    when(hospService.getHospLastUpdate()).thenReturn(LocalDateTime.now().minusDays(1));
    when(hospUppdateringEntityRepository.findHospUppdatering()).thenReturn(
        Optional.of(new HospUppdateringEntity(LocalDateTime.now()))
    );

    final var actual = hospRepository.needUpdateFromHosp();
    assertFalse(actual, "Expected false when hosp last update is before stored hosp update");
  }

  @Test
  void shouldUpdateHospUpdatedWhenUpdateHasBeenStoredBefore() {
    final var hospLastUpdate = LocalDateTime.now();
    when(hospService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    when(hospUppdateringEntityRepository.findHospUppdatering()).thenReturn(
        Optional.of(new HospUppdateringEntity(LocalDateTime.now().minusDays(1)))
    );

    hospRepository.hospUpdated();

    verify(hospUppdateringEntityRepository).save(
        new HospUppdateringEntity(hospLastUpdate)
    );
  }

  @Test
  void shouldUpdateHospUpdatedWhenNotUpdateHasBeenStoredBefore() {
    final var hospLastUpdate = LocalDateTime.now();
    when(hospService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    when(hospUppdateringEntityRepository.findHospUppdatering()).thenReturn(Optional.empty());

    hospRepository.hospUpdated();

    verify(hospUppdateringEntityRepository).save(
        new HospUppdateringEntity(hospLastUpdate)
    );
  }

  @Test
  void shouldNotUpdateHospUpdatedWhenHospLastUpdateIsNull() {
    when(hospService.getHospLastUpdate()).thenReturn(null);

    hospRepository.hospUpdated();

    verifyNoInteractions(hospUppdateringEntityRepository);
  }
}