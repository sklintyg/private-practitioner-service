package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.CURRENT_MEDGIVANDE_TEXT_ENTITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.CURRENT_CONSENT_FORM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsentFormRepositoryTest {

  @Mock
  private MedgivandeTextEntityRepository medgivandeTextEntityRepository;
  @InjectMocks
  private ConsentFormRepository consentFormRepository;

  @Test
  void shouldReturnLatestFormAsCurrent() {
    when(medgivandeTextEntityRepository.findLatest()).thenReturn(CURRENT_MEDGIVANDE_TEXT_ENTITY);

    final var actual = consentFormRepository.current();

    assertEquals(CURRENT_CONSENT_FORM, actual);
  }
}