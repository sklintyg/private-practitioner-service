package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import lombok.Builder;
import lombok.Data;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospInformation;

@Data
@Builder
public class HospInformationResponse {

  HospInformation hospInformation;
}
