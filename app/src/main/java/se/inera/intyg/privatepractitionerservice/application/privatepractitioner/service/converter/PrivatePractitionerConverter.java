package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;

@Component
public class PrivatePractitionerConverter {

  public PrivatePractitionerDTO convert(PrivatePractitioner privatePractitioner) {
    return PrivatePractitionerDTO.builder()
        .hsaId(privatePractitioner.getHsaId())
        .personId(privatePractitioner.getPersonId())
        .name(privatePractitioner.getName())
        .email(privatePractitioner.getEmail())
        .careProviderName(privatePractitioner.getCareProviderName())
        .registrationDate(privatePractitioner.getRegistrationDate())
        .build();
  }
}
