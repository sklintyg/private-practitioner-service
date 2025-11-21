package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataAssert.assertPrivatlakareEntity;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.DR_KRANSTEGE_ENTITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.kranstegeEntityBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.PrivatlakareEntityConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerRepositoryTest {

  @Mock
  private PrivatlakareEntityRepository privatlakareEntityRepository;
  @Mock
  private PrivatlakareIdEntityRepository privatlakareIdEntityRepository;
  @Mock
  private PrivatlakareEntityConverter privatlakareEntityConverter;
  @InjectMocks
  private PrivatePractitionerRepository privatePractitionerRepository;

  @Test
  void shouldReturnPrivatePractitionerIfFindByPersonId() {
    final var personId = "personId";
    final var expected = PrivatePractitioner.builder()
        .personId(personId)
        .build();

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .personId(personId)
        .build();
    when(privatlakareEntityRepository.findByPersonId(personId)).thenReturn(privatlakareEntity);
    when(privatlakareEntityConverter.convert(privatlakareEntity)).thenReturn(expected);

    final var actual = privatePractitionerRepository.findByPersonId(personId);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldReturnEpmtyIfNotFindByPersonId() {
    final var personId = "personId";

    when(privatlakareEntityRepository.findByPersonId(personId)).thenReturn(null);

    final var actual = privatePractitionerRepository.findByPersonId(personId);

    assertTrue(actual.isEmpty(), "Expected empty result when personId not found");
  }

  @Test
  void shouldReturnPrivatePractitionerIfFindByHsaId() {
    final var hsaId = "hsaId";
    final var expected = PrivatePractitioner.builder()
        .hsaId(hsaId)
        .build();

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .hsaId(hsaId)
        .build();
    when(privatlakareEntityRepository.findByHsaId(hsaId)).thenReturn(privatlakareEntity);
    when(privatlakareEntityConverter.convert(privatlakareEntity)).thenReturn(expected);

    final var actual = privatePractitionerRepository.findByHsaId(hsaId);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldReturnEmptyIfNotFindByPersonId() {
    final var hsaId = "hsaId";

    when(privatlakareEntityRepository.findByHsaId(hsaId)).thenReturn(null);

    final var actual = privatePractitionerRepository.findByHsaId(hsaId);

    assertTrue(actual.isEmpty(), "Expected empty result when hsaId not found");
  }

  @Test
  void shouldReturnListOfPrivatePractitioners() {
    final var expected = List.of(
        PrivatePractitioner.builder().hsaId("HSA123").build(),
        PrivatePractitioner.builder().hsaId("HSA456").build()
    );

    final var privatlakareEntities = List.of(
        PrivatlakareEntity.builder().hsaId("HSA123").build(),
        PrivatlakareEntity.builder().hsaId("HSA456").build()
    );

    when(privatlakareEntityRepository.findAll()).thenReturn(privatlakareEntities);
    when(privatlakareEntityConverter.convert(privatlakareEntities.getFirst()))
        .thenReturn(expected.getFirst());
    when(privatlakareEntityConverter.convert(privatlakareEntities.getLast()))
        .thenReturn(expected.getLast());

    final var actual = privatePractitionerRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnEmptyListOfPrivatePractitioners() {
    final var expected = List.of();

    when(privatlakareEntityRepository.findAll()).thenReturn(List.of());

    final var actual = privatePractitionerRepository.findAll();

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnTrueIfPrivatePractitionerExists() {
    when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(DR_KRANSTEGE_ENTITY);

    final var actual = privatePractitionerRepository.isExists(DR_KRANSTEGE_PERSON_ID);

    assertTrue(actual, "Expected exists to be true when personId found");
  }

  @Test
  void shouldReturnFalseIfPrivatePractitionerNotExists() {
    when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(null);

    final var actual = privatePractitionerRepository.isExists(DR_KRANSTEGE_PERSON_ID);

    assertTrue(!actual, "Expected exists to be false when personId not found");
  }

  @Nested
  class SavePrivatePractitionerTests {

    @Nested
    class WhenCreatingNewPrivatePractitioner {

      @BeforeEach
      void setUp() {
        when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE.getPersonId()))
            .thenReturn(null);
        when(privatlakareIdEntityRepository.save(new PrivatlakareIdEntity())).thenReturn(
            PrivatlakareIdEntity.builder().id(1).build()
        );
        when(privatlakareEntityRepository.save(any(PrivatlakareEntity.class)))
            .thenReturn(DR_KRANSTEGE_ENTITY);
        when(privatlakareEntityConverter.convert(DR_KRANSTEGE_ENTITY))
            .thenReturn(DR_KRANSTEGE);
      }

      @Test
      void shouldReturnCreatedSavedPrivatePractitioner() {
        final var actual = privatePractitionerRepository.save(DR_KRANSTEGE);
        assertEquals(DR_KRANSTEGE, actual);
      }

      @Test
      void shouldSaveCreatedPrivatePractitioner() {
        final var captor = ArgumentCaptor.forClass(PrivatlakareEntity.class);
        privatePractitionerRepository.save(DR_KRANSTEGE);
        final var newKranstegeHsaid = "SE165565594230-WEBCERT00001";
        final var newKranstegeEntity = kranstegeEntityBuilder()
            .hsaId(newKranstegeHsaid)
            .enhetsId(newKranstegeHsaid)
            .vardgivareId(newKranstegeHsaid)
            .build();
        verify(privatlakareEntityRepository).save(captor.capture());
        assertPrivatlakareEntity(newKranstegeEntity, captor.getValue());
      }
    }

    @Nested
    class WhenUpdatingPrivatePractitioner {

      @BeforeEach
      void setUp() {
        final var existingEntity = PrivatlakareEntity.builder()
            .personId(DR_KRANSTEGE_PERSON_ID)
            .hsaId(DR_KRANSTEGE_HSA_ID)
            .enhetsId(DR_KRANSTEGE_HSA_ID)
            .vardgivareId(DR_KRANSTEGE_HSA_ID)
            .registreringsdatum(LocalDateTime.now())
            .godkandAnvandare(true)
            .build();

        when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE.getPersonId()))
            .thenReturn(existingEntity);
        when(privatlakareEntityRepository.save(any(PrivatlakareEntity.class)))
            .thenReturn(DR_KRANSTEGE_ENTITY);
        when(privatlakareEntityConverter.convert(DR_KRANSTEGE_ENTITY))
            .thenReturn(DR_KRANSTEGE);
      }

      @Test
      void shouldReturnUpdatedSavedPrivatePractitioner() {
        final var actual = privatePractitionerRepository.save(DR_KRANSTEGE);
        assertEquals(DR_KRANSTEGE, actual);
      }

      @Test
      void shouldSaveUpdatedPrivatePractitioner() {
        final var captor = ArgumentCaptor.forClass(PrivatlakareEntity.class);
        privatePractitionerRepository.save(DR_KRANSTEGE);

        verify(privatlakareEntityRepository).save(captor.capture());
        assertPrivatlakareEntity(DR_KRANSTEGE_ENTITY, captor.getValue());
      }
    }
  }
}