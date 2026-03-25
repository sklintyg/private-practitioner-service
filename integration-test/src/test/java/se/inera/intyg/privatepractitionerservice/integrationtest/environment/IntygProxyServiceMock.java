/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonSvarDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@RequiredArgsConstructor
public class IntygProxyServiceMock {

  private final MockServerClient mockServerClient;

  public void credentialsForPersonResponse(
      GetCredentialsForPersonResponseDTO credentialsForPerson) {
    try {
      mockServerClient.clear(HttpRequest.request("/api/v1/credentialsForPerson"));
      mockServerClient
          .when(HttpRequest.request("/api/v1/credentialsForPerson"))
          .respond(
              HttpResponse.response(new ObjectMapper().writeValueAsString(credentialsForPerson))
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void certificationPersonResponse(GetHospCertificationPersonResponseDTO response) {
    try {
      mockServerClient
          .when(HttpRequest.request("/api/v1/certificationPerson"))
          .respond(
              HttpResponse.response(new ObjectMapper().writeValueAsString(response))
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void personResponse(PersonSvarDTO response) {
    try {
      mockServerClient
          .when(HttpRequest.request("/api/v1/person"))
          .respond(
              HttpResponse.response(new ObjectMapper().writeValueAsString(response))
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void lastUpdate() {
    try {
      mockServerClient
          .when(HttpRequest.request("/api/v1/lastUpdate"))
          .respond(
              HttpResponse.response("{\"lastUpdate\":\"2024-01-01T12:00:00\"}")
                  .withStatusCode(200)
                  .withContentType(MediaType.APPLICATION_JSON));
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static GetCredentialsForPersonResponseDTOBuilder fridaKranstegeCredentialsBuilder() {
    return GetCredentialsForPersonResponseDTO.builder()
        .credentials(fridaKranstegeHospCredentials().build());
  }

  public static HospCredentialsForPersonBuilder fridaKranstegeHospCredentials() {
    return HospCredentialsForPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .personalPrescriptionCode(DR_KRANSTEGE_PRESCRIPTION_CODE)
        .healthCareProfessionalLicence(
            DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS.stream()
                .map(
                    licensedHealtcareProfession ->
                        HealthCareProfessionalLicence.builder()
                            .healthCareProfessionalLicenceCode(licensedHealtcareProfession.code())
                            .healthCareProfessionalLicenceName(licensedHealtcareProfession.name())
                            .build())
                .toList())
        .healthCareProfessionalLicenceSpeciality(
            DR_KRANSTEGE_SPECIALITIES.stream()
                .map(
                    speciality ->
                        HCPSpecialityCodes.builder()
                            .specialityCode(speciality.code())
                            .specialityName(speciality.name())
                            .healthCareProfessionalLicenceCode("LK")
                            .build())
                .toList());
  }

  public static GetHospCertificationPersonResponseDTOBuilder addToCertifierResponseBuilder() {
    return GetHospCertificationPersonResponseDTO.builder()
        .result(
            Result.builder()
                .resultCode("OK")
                .resultText("Successfully added to certifier")
                .build());
  }

  public static GetHospCertificationPersonResponseDTOBuilder removeFromCertifierResponseBuilder(
      String resultCode) {
    return GetHospCertificationPersonResponseDTO.builder()
        .result(Result.builder().resultCode(resultCode).resultText("").build());
  }

  public static PersonSvarDTO fridaKranstegePerson() {
    return new PersonSvarDTO(new PersonDTO("197705232382", "Frida", "Andersson"), StatusDTO.FOUND);
  }

  public static PersonSvarDTO fridaKranstegePersonWithSameName() {
    return new PersonSvarDTO(new PersonDTO("197705232382", "Frida", "Kranstege"), StatusDTO.FOUND);
  }
}
