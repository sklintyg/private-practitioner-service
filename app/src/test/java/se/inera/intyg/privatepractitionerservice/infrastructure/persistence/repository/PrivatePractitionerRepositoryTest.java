package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.PrivatlakareEntityConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerRepositoryTest {

  @Mock
  private PrivatlakareEntityRepository privatlakareEntityRepository;
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
}