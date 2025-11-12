package se.inera.intyg.privatepractitionerservice.infrastructure.codesystem;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeSystemRepositoryTest {

  @InjectMocks
  private CodeSystemRepository codeSystemRepository;

  @Test
  void shouldReturnHealthcareServiceTypeCodes() {
    final var actual = codeSystemRepository.getHealthcareServiceTypeCodes();
    assertAll(
        () -> assertNotNull(actual, "HealthcareServiceType codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "HealthcareServiceType codes should not be empty")
    );
  }

  @Test
  void shouldReturnPositionCodes() {
    final var actual = codeSystemRepository.getPositionCodes();
    assertAll(
        () -> assertNotNull(actual, "Position codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "Position codes should not be empty")
    );
  }

  @Test
  void shouldReturnTypeOfCareCodes() {
    final var actual = codeSystemRepository.getTypeOfCareCodes();
    assertAll(
        () -> assertNotNull(actual, "TypeOfCare codes should not be null"),
        () -> assertFalse(actual.isEmpty(), "TypeOfCare codes should not be empty")
    );
  }
}