/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.privatlakarportal.integration.privatepractitioner.services;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static io.restassured.config.SSLConfig.sslConfig;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import se.inera.intyg.privatlakarportal.common.model.Registration;

@Disabled("Disabled until we have introduced a new internal api to handle registrations")
public class ErasePrivatePractitionerIT extends BaseRestIntegrationTest {

    private static final String INTERNAL_BASE_URI = System.getProperty(
        "integration.tests.actuatorUrl", "http://localhost:8081");
    private static final String ERASE_PRIVATE_PRACTITIONER_URL =
        INTERNAL_BASE_URI + "/internalapi/privatepractitioner/erase";

    private static final String PERSONNUMMER = "195107132119";
    private static final String FORNAMN = "Valdemar";
    private static final String EFTERNAMN = "Eriksson";

    private String hsaId;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        setupRestAssured();
        RestAssured.sessionId = createAuthSession(FORNAMN, EFTERNAMN, PERSONNUMMER);
        createPrivatePractitioner();
    }

    @AfterEach
    public void cleanup() {
        deletePrivatePractitioner();
        hsaId = null;
        RestAssured.reset();
    }

    @Test
    public void shouldErasePrivatePractitioner() {
        assertEquals(hsaId, getPrivatePractitioner().get("hsaId"));
        erasePrivatePractitioner(hsaId);
        assertThrows(JsonPathException.class, () -> getPrivatePractitioner().get("hsaId"));
    }

    @Test
    public void shouldHandlePrivatePractitionerNotFound() {
        assertDoesNotThrow(() -> erasePrivatePractitioner("NotAPrivatePractitioner"));
    }

    private void erasePrivatePractitioner(String hsaId) {
        given()
            .pathParam("id", hsaId)
            .when().delete(ERASE_PRIVATE_PRACTITIONER_URL + "/{id}")
            .then().statusCode(200);
    }

    private void createPrivatePractitioner() throws JsonProcessingException {
        insertHospInformation();
        updatePrivatePractitionerName();
        registerPrivatePractitioner();

        hsaId = getPrivatePractitioner().get("hsaId");
    }

    private JsonPath getPrivatePractitioner() {
        return spec()
            .pathParam("id", PERSONNUMMER)
            .when().get("/api/test/registration/{id}")
            .then().statusCode(200)
            .extract().body().jsonPath();
    }

    // TODO: This will have to be changed to the new registration internal api
    private void registerPrivatePractitioner() {
        spec()
//            .body(createRegistrationRequest())
            .when().post("api/registration/create")
            .then().statusCode(200);
    }

    private void updatePrivatePractitionerName() {
        spec().get("api/user");
    }

    private void insertHospInformation() throws JsonProcessingException {
        spec()
            .body(createHospInformation())
            .when().post("api/test/hosp/add")
            .then().statusCode(200);
    }

    private void deletePrivatePractitioner() {
        spec()
            .pathParam("id", PERSONNUMMER)
            .when().delete("/api/test/registration/remove/{id}")
            .then().statusCode(200);
    }

//    private CreateRegistrationRequest createRegistrationRequest() {
//        final var registrationRequest = new CreateRegistrationRequest();
//        registrationRequest.setRegistration(getRegistration());
//        registrationRequest.setGodkantMedgivandeVersion(1L);
//
//        return registrationRequest;
//    }

    private Registration getRegistration() {
        final var registration = new Registration();
        registration.setVerksamhetensNamn("Test verksamhet");
        registration.setArbetsplatskod("0000000");
        registration.setAgarForm("Privat");
        registration.setAdress("Test adress");
        registration.setPostnummer("12345");
        registration.setPostort("Test postort");
        registration.setTelefonnummer("123456789");
        registration.setEpost("test@example.com");
        registration.setBefattning("201010");
        registration.setVerksamhetstyp("10");
        registration.setVardform("01");
        registration.setLan("Test län");
        registration.setKommun("Test kommun");

        return registration;
    }

    private String createHospInformation() throws JsonProcessingException {
        final var objectMapper = new ObjectMapper();

        final var body = Map.of(
            "personalIdentityNumber", PERSONNUMMER,
            "personalPrescriptionCode", "1234567",
            "educationCodes", Collections.emptyList(),
            "restrictions", Collections.emptyList(),
            "specialities", getSpecialities(),
            "healthCareProfessionalLicenceType", getLicences()
        );

        return objectMapper.writeValueAsString(body);
    }

    private List<Map<String, String>> getLicences() {
        return List.of(
            Map.of("healthCareProfessionalLicenceCode", "LK", "healthCareProfessionalLicenceName",
                "Läkare"));
    }

    private List<Map<String, String>> getSpecialities() {
        return List.of(
            Map.of("specialityCode", "32", "specialityName", "Klinisk fysiologi"),
            Map.of("specialityCode", "74", "specialityName", "Nukleärmedicin"));
    }

    private void setupRestAssured() {
        RestAssured.reset();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = System.getProperty("integration.tests.baseUrl", "http://localhost:8060");
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON)
            .build();
        RestAssured.config = newConfig().sslConfig(sslConfig().allowAllHostnames())
            .sessionConfig(RestAssured.config().getSessionConfig().sessionIdName("SESSION"));
    }
}
