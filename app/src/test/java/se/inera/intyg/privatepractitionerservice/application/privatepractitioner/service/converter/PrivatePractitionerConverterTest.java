package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerConverterTest {

  @InjectMocks
  private PrivatePractitionerConverter privatePractitionerConverter;

  @Test
  void shouldReturnHsaId() {
    final var expected = "hsaId";

    final var privatePractitioner = PrivatePractitioner.builder()
        .hsaId(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getHsaId());
  }

  @Test
  void shouldReturnPersonId() {
    final var expected = "personId";

    final var privatePractitioner = PrivatePractitioner.builder()
        .personId(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getPersonId());
  }

  @Test
  void shouldReturnName() {
    final var expected = "name";

    final var privatePractitioner = PrivatePractitioner.builder()
        .name(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getName());
  }

  @Test
  void shouldReturnEmail() {
    final var expected = "email";

    final var privatePractitioner = PrivatePractitioner.builder()
        .email(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getEmail());
  }

  @Test
  void shouldReturnCareProviderName() {
    final var expected = "careProviderName";

    final var privatePractitioner = PrivatePractitioner.builder()
        .careProviderName(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getCareProviderName());
  }

  @Test
  void shouldReturnRegistrationDate() {
    final var expected = LocalDateTime.now();

    final var privatePractitioner = PrivatePractitioner.builder()
        .registrationDate(expected)
        .build();

    final var actual = privatePractitionerConverter.convert(privatePractitioner);

    assertEquals(expected, actual.getRegistrationDate());
  }
}