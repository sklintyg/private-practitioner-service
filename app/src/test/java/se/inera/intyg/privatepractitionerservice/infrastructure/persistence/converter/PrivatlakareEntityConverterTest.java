package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@ExtendWith(MockitoExtension.class)
class PrivatlakareEntityConverterTest {

  @InjectMocks
  private PrivatlakareEntityConverter privatlakareEntityConverter;

  @Test
  void shouldReturnHsaId() {
    final var expected = "hsaId";

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .hsaId(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getHsaId());
  }

  @Test
  void shouldReturnPersonId() {
    final var expected = "personId";

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .personId(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getPersonId());
  }

  @Test
  void shouldReturnName() {
    final var expected = "name";

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .fullstandigtNamn(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getName());
  }

  @Test
  void shouldReturnCareProviderName() {
    final var expected = "careProviderName";

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .vardgivareNamn(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getCareProviderName());
  }

  @Test
  void shouldReturnEmail() {
    final var expected = "email";

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .epost(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getEmail());
  }

  @Test
  void shouldReturnRegistrationDate() {
    final var expected = LocalDateTime.now();

    final var privatlakareEntity = PrivatlakareEntity.builder()
        .registreringsdatum(expected)
        .build();

    final var actual = privatlakareEntityConverter.convert(privatlakareEntity);

    assertEquals(expected, actual.getRegistrationDate());
  }
}