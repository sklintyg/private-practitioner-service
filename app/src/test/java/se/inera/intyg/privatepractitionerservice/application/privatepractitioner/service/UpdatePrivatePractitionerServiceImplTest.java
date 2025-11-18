package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_UPDATE_REQUEST;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.UpdatePrivatePractitionerRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class UpdatePrivatePractitionerServiceImplTest {

  @Mock
  private PrivatePractitionerRepository repository;
  @Mock
  private UpdatePrivatePractitionerRequestValidator validator;
  @Mock
  private HashUtility hashUtility;
  @InjectMocks
  private UpdatePrivatePractitionerServiceImpl service;

  @Test
  void shouldThrowExceptionWhenValidationFails() {
    doThrow(PrivatlakarportalServiceException.class).when(validator)
        .validate(DR_KRANSTEGE_UPDATE_REQUEST);
    assertThrows(PrivatlakarportalServiceException.class,
        () -> service.update(DR_KRANSTEGE_UPDATE_REQUEST));
  }

  @Test
  void shouldThrowExceptionWhenPrivatePractitionerNotFound() {
    assertThrows(PrivatlakarportalServiceException.class,
        () -> service.update(DR_KRANSTEGE_UPDATE_REQUEST));
  }

  @Test
  void shouldReturnSavedPrivatePractitioner() {
    when(repository.isExists(DR_KRANSTEGE_UPDATE_REQUEST.getPersonId())).thenReturn(true);
    final var result = service.update(DR_KRANSTEGE_UPDATE_REQUEST);

    assertEquals(PrivatePractitionerDTO.builder().build(), result);
  }

}