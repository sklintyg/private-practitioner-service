package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NO_ACCOUNT;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.OK;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class ValidatePrivatePractitionerService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HashUtility hashUtility;
  private final HospRepository hospRepository;

  public ValidatePrivatePractitionerResponse validate(String personId) {
    final var existingPrivatePractitioner = privatePractitionerRepository.findByPersonId(personId);
    if (existingPrivatePractitioner.isEmpty()) {
      return ValidatePrivatePractitionerResponse.builder()
          .resultCode(NO_ACCOUNT)
          .resultText("No private practitioner with personId '%s' exists."
              .formatted(hashUtility.hash(personId))
          )
          .build();
    }

    final var privatePractitioner = existingPrivatePractitioner.get();
    final var hospPerson = hospRepository.updatedHospPerson(privatePractitioner);
    hospPerson.ifPresent(hosp -> {
          privatePractitioner.updateWithHospInformation(hosp);
          privatePractitionerRepository.save(privatePractitioner);
        }
    );

    if (privatePractitioner.isLicensedPhysician()) {
      if (privatePractitioner.isFirstLogin()) {
        privatePractitioner.firstLogin();
        privatePractitionerRepository.save(privatePractitioner);
      }
      return ValidatePrivatePractitionerResponse.builder()
          .resultCode(OK)
          .build();
    }

    return ValidatePrivatePractitionerResponse.builder()
        .resultCode(NOT_AUTHORIZED_IN_HOSP)
        .resultText("Private practitioner with personId '%s' is not authorized to use webcert."
            .formatted(hashUtility.hash(personId))
        )
        .build();
  }
}
