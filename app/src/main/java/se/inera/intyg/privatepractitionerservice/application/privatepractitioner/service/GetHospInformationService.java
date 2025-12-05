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

    if (hospInformation.hasHospInformation()) {
      return
          GetHospInformationResponse.builder()
              .personId(hospInformation.getPersonalIdentityNumber())
              .personalPrescriptionCode(hospInformation.getPersonalPrescriptionCode())
              .licensedHealthcareProfessions(
                  hospInformation.getLicensedHealthcareProfessions().stream()
                      .map(
                          license -> new CodeDTO(
                              license.code(),
                              license.name()
                          )
                      )
                      .toList()
              )
              .specialities(
                  hospInformation.getSpecialities().stream()
                      .map(
                          speciality -> new CodeDTO(
                              speciality.code(),
                              speciality.name()
                          )
                      )
                      .toList()
              )
              .build();
    } else {
      return GetHospInformationResponse.builder()
          .personId(request.getPersonId())
          .build();
    }
  }
}
