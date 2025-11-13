package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CodeDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.RegistrationConfigurationResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.CodeSystemRepository;

@Service
@RequiredArgsConstructor
public class RegistrationConfigurationService {

  private final CodeSystemRepository codeSystemRepository;

  public RegistrationConfigurationResponse get() {
    return RegistrationConfigurationResponse.builder()
        .healthcareServiceTypeCodes(
            codeSystemRepository.getHealthcareServiceTypeCodes().stream()
                .map(healthcareServiceType -> new CodeDTO(
                    healthcareServiceType.code(),
                    healthcareServiceType.name())
                )
                .toList()
        )
        .positionCodes(
            codeSystemRepository.getPositionCodes().stream()
                .map(position -> new CodeDTO(
                    position.code(),
                    position.name())
                )
                .toList()
        )
        .typeOfCareCodes(
            codeSystemRepository.getTypeOfCareCodes().stream()
                .map(typeOfCare -> new CodeDTO(
                    typeOfCare.code(),
                    typeOfCare.name())
                )
                .toList()
        )
        .build();
  }
}
