package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.CONSENT_FORM_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.POSITION_SPECIALIST_DOCTOR_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.TYPE_OF_CARE_OUTPATIENT_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.CURRENT_CONSENT_FORM;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.POSITION_SPECIALIST_DOCTOR;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.TYPE_OF_CARE_OUTPATIENT;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.ConsentFormRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationConfigurationServiceTest {

  @Mock
  private CodeSystemRepository codeSystemRepository;
  @Mock
  private ConsentFormRepository consentFormRepository;
  @InjectMocks
  private RegistrationConfigurationService registrationConfigurationService;

  @BeforeEach
  void setUp() {
    when(consentFormRepository.current()).thenReturn(CURRENT_CONSENT_FORM);
    when(codeSystemRepository.getHealthcareServiceTypeCodes())
        .thenReturn(List.of(HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE));
    when(codeSystemRepository.getPositionCodes()).thenReturn(List.of(POSITION_SPECIALIST_DOCTOR));
    when(codeSystemRepository.getTypeOfCareCodes()).thenReturn(List.of(TYPE_OF_CARE_OUTPATIENT));
  }

  @Test
  void shouldReturnCurrentConsentForm() {
    final var actual = registrationConfigurationService.get();
    assertEquals(CONSENT_FORM_DTO, actual.getConsentForm());
  }

  @Test
  void shouldReturnHealthcareServiceTypesCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getHealthcareServiceTypeCodes().size());
    assertEquals(HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE_DTO,
        actual.getHealthcareServiceTypeCodes().getFirst()
    );
  }

  @Test
  void shouldReturnPositionCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getPositionCodes().size());
    assertEquals(POSITION_SPECIALIST_DOCTOR_DTO,
        actual.getPositionCodes().getFirst()
    );
  }

  @Test
  void shouldReturnTypeOfCareCodes() {
    final var actual = registrationConfigurationService.get();
    assertEquals(1, actual.getTypeOfCareCodes().size());
    assertEquals(TYPE_OF_CARE_OUTPATIENT_DTO,
        actual.getTypeOfCareCodes().getFirst()
    );
  }
}