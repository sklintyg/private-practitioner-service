package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CodeDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;

@Service
@RequiredArgsConstructor
public class GetHospInformationService {

  private final HospRepository hospRepository;

  public GetHospInformationResponse get(GetHospInformationRequest request) {
    if (request.getPersonId() == null || request.getPersonId().isBlank()) {
      throw new IllegalArgumentException("Person Id is required");
    }

    final var hospInformation = hospRepository.findByPersonId(request.getPersonId());

    return hospInformation.map(
            hosp -> GetHospInformationResponse.builder()
                .personId(hosp.getPersonalIdentityNumber())
                .personalPrescriptionCode(hosp.getPersonalPrescriptionCode())
                .licensedHealthcareProfessions(
                    hosp.getLicensedHealthcareProfessions().stream()
                        .map(
                            license -> new CodeDTO(
                                license.code(),
                                license.name()
                            )
                        )
                        .toList()
                )
                .specialities(
                    hosp.getSpecialities().stream()
                        .map(
                            speciality -> new CodeDTO(
                                speciality.code(),
                                speciality.name()
                            )
                        )
                        .toList()
                )
                .build()
        )
        .orElseGet(
            () -> GetHospInformationResponse.builder()
                .personId(request.getPersonId())
                .build()
        );
  }
}
