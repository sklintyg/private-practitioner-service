package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NO_ACCOUNT;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class ValidatePrivatePractitionerService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HashUtility hashUtility;

  public ValidatePrivatePractitionerResponse validate(String personId) {
    final var privatePractitioner = privatePractitionerRepository.findByPersonId(personId);
    if (privatePractitioner.isEmpty()) {
      return ValidatePrivatePractitionerResponse.builder()
          .resultCode(NO_ACCOUNT)
          .resultText(
              "No private practitioner with personal identity number: %s exists."
                  .formatted(hashUtility.hash(personId))
          )
          .build();
    }

    // TODO: Check if update from hosp
    if (privatePractitioner.get().isLicensedPhysician()) {
      // TODO: Check first login
      return ValidatePrivatePractitionerResponse.builder()
          .resultCode(OK)
          .build();
    }

    return ValidatePrivatePractitionerResponse.builder()
        .resultCode(NOT_AUTHORIZED_IN_HOSP)
        .resultText(
            "Private practitioner with personal identity number: %s is not authorized to use webcert."
                .formatted(hashUtility.hash(personId))
        )
        .build();
  }
}
