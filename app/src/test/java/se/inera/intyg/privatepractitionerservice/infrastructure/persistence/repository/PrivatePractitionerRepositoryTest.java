package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataAssert.assertPrivatlakareEntity;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.DR_KRANSTEGE_ENTITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.kranstegeEntityBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
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
  @Mock
  private HashUtility hashUtility;
  @Mock
  private MailService mailService;
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
    when(privatlakareEntityRepository.findByPersonId(personId)).thenReturn(
        Optional.of(privatlakareEntity)
    );
    when(privatlakareEntityConverter.convert(privatlakareEntity)).thenReturn(expected);

    final var actual = privatePractitionerRepository.findByPersonId(personId);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldReturnEpmtyIfNotFindByPersonId() {
    final var personId = "personId";

    when(privatlakareEntityRepository.findByPersonId(personId)).thenReturn(Optional.empty());

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
    when(privatlakareEntityRepository.findByHsaId(hsaId)).thenReturn(
        Optional.of(privatlakareEntity)
    );
    when(privatlakareEntityConverter.convert(privatlakareEntity)).thenReturn(expected);

    final var actual = privatePractitionerRepository.findByHsaId(hsaId);

    assertEquals(expected, actual.orElseThrow());
  }

  @Test
  void shouldReturnEmptyIfNotFindByPersonId() {
    final var hsaId = "hsaId";

    when(privatlakareEntityRepository.findByHsaId(hsaId)).thenReturn(Optional.empty());

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
        .thenReturn(Optional.of(DR_KRANSTEGE_ENTITY));

    final var actual = privatePractitionerRepository.isExists(DR_KRANSTEGE_PERSON_ID);

    assertTrue(actual, "Expected exists to be true when personId found");
  }

  @Test
  void shouldReturnFalseIfPrivatePractitionerNotExists() {
    when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.empty());

    final var actual = privatePractitionerRepository.isExists(DR_KRANSTEGE_PERSON_ID);

    assertFalse(actual, "Expected exists to be false when personId not found");
  }

  @Nested
  class SavePrivatePractitionerTests {

    @Nested
    class WhenCreatingNewPrivatePractitioner {

      PrivatePractitioner kranstege;

      @BeforeEach
      void setUp() {
        kranstege = kranstegeBuilder().build();
        when(privatlakareEntityRepository.findByPersonId(kranstege.getPersonId()))
            .thenReturn(Optional.empty());
        when(privatlakareIdEntityRepository.save(new PrivatlakareIdEntity())).thenReturn(
            PrivatlakareIdEntity.builder().id(1).build());
        when(privatlakareEntityRepository.save(any(PrivatlakareEntity.class)))
            .thenReturn(DR_KRANSTEGE_ENTITY);
        when(privatlakareEntityConverter.convert(DR_KRANSTEGE_ENTITY)).thenReturn(kranstege);
        ReflectionTestUtils.setField(privatePractitionerRepository, "hsaIdNotificationInterval",
            50);
      }

      @Test
      void shouldReturnCreatedSavedPrivatePractitioner() {
        final var actual = privatePractitionerRepository.save(kranstege);
        assertEquals(kranstege, actual);
      }

      @Test
      void shouldSaveCreatedPrivatePractitioner() {
        final var captor = ArgumentCaptor.forClass(PrivatlakareEntity.class);
        privatePractitionerRepository.save(kranstege);
        final var newKranstegeHsaid = "SE165565594230-WEBCERT00001";
        final var newKranstegeEntity = kranstegeEntityBuilder()
            .hsaId(newKranstegeHsaid)
            .enhetsId(newKranstegeHsaid)
            .vardgivareId(newKranstegeHsaid)
            .build();
        verify(privatlakareEntityRepository).save(captor.capture());
        assertPrivatlakareEntity(newKranstegeEntity, captor.getValue());
      }

      @Test
      void shouldSendAdminMailAfterSpecifiedAmountOfRegistrations() {
        when(privatlakareIdEntityRepository.findLatestGeneratedHsaId()).thenReturn(50);
        privatePractitionerRepository.save(kranstege);
        verify(mailService).sendHsaGenerationEmail(50);
      }

      @Test
      void shouldNotSendAdminMailForEachRegistration() {
        when(privatlakareIdEntityRepository.findLatestGeneratedHsaId()).thenReturn(49);
        privatePractitionerRepository.save(kranstege);
        verifyNoInteractions(mailService);
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

        when(privatlakareEntityRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID)).thenReturn(
            Optional.of(existingEntity));
        when(privatlakareEntityRepository.save(any(PrivatlakareEntity.class)))
            .thenReturn(DR_KRANSTEGE_ENTITY);
        when(privatlakareEntityConverter.convert(DR_KRANSTEGE_ENTITY))
            .thenReturn(DR_KRANSTEGE);
      }

      @Test
      void shouldReturnUpdatedSavedPrivatePractitioner() {
        final var actual = privatePractitionerRepository.save(kranstegeBuilder().build());
        assertEquals(DR_KRANSTEGE, actual);
      }

      @Test
      void shouldSaveUpdatedPrivatePractitioner() {
        final var captor = ArgumentCaptor.forClass(PrivatlakareEntity.class);
        privatePractitionerRepository.save(kranstegeBuilder().build());

        verify(privatlakareEntityRepository).save(captor.capture());
        assertPrivatlakareEntity(DR_KRANSTEGE_ENTITY, captor.getValue());
      }
    }
  }
}