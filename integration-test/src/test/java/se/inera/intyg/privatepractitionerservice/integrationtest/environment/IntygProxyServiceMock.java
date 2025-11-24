package se.inera.intyg.privatepractitionerservice.integrationtest.environment;

import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PRESCRIPTION_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson.HospCredentialsForPersonBuilder;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonResponseDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetCredentialsForPersonResponseDTO.GetCredentialsForPersonResponseDTOBuilder;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonResponseDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonResponseDTO.GetHospCertificationPersonResponseDTOBuilder;

@RequiredArgsConstructor
public class IntygProxyServiceMock {

  private final MockServerClient mockServerClient;

  public void credentialsForPersonResponse(
      GetCredentialsForPersonResponseDTO credentialsForPerson) {
    try {
      mockServerClient.when(HttpRequest.request("/api/v1/credentialsForPerson"))
          .respond(
              HttpResponse
                  .response(
                      new ObjectMapper().writeValueAsString(
                          credentialsForPerson
                      )
                  )
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON)
          );
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void certificationPersonResponse(GetHospCertificationPersonResponseDTO response) {
    try {
      mockServerClient.when(HttpRequest.request("/api/v1/certificationPerson"))
          .respond(
              HttpResponse
                  .response(
                      new ObjectMapper().writeValueAsString(
                          response
                      )
                  )
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON)
          );
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static GetCredentialsForPersonResponseDTOBuilder fridaKranstegeCredentialsBuilder() {
    return GetCredentialsForPersonResponseDTO.builder()
        .credentials(
            fridaKranstegeHospBuilder().build()
        );
  }

  public static HospCredentialsForPersonBuilder fridaKranstegeHospBuilder() {
    return HospCredentialsForPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .personalPrescriptionCode(DR_KRANSTEGE_PRESCRIPTION_CODE)
        .healthCareProfessionalLicence(
            DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS.stream()
                .map(licensedHealtcareProfession ->
                    HealthCareProfessionalLicence.builder()
                        .healthCareProfessionalLicenceCode(
                            licensedHealtcareProfession.code()
                        )
                        .healthCareProfessionalLicenceName(
                            licensedHealtcareProfession.name()
                        )
                        .build()
                )
                .toList()
        )
        .healthCareProfessionalLicenceSpeciality(
            DR_KRANSTEGE_SPECIALITIES.stream()
                .map(speciality ->
                    HCPSpecialityCodes.builder()
                        .specialityCode(speciality.code())
                        .specialityName(speciality.name())
                        .build()
                )
                .toList()
        );
  }

  public static GetHospCertificationPersonResponseDTOBuilder addToCertifierResponseBuilder() {
    return GetHospCertificationPersonResponseDTO.builder()
        .result(
            Result.builder()
                .resultCode("OK")
                .resultText("Successfully added to certifier")
                .build()
        );
  }
}
