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

import com.google.common.collect.Lists;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import se.inera.intyg.privatlakarportal.common.model.Registration;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.CreateRegistrationRequest;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by stillor on 2/3/17.
 */
public class HospUppdateringIT extends BaseRestIntegrationTest {

  public static final String PERSONNUMMER = "195206172339";
  public static final String FORNAMN = "Björn Anders Daniel";
  public static final String EFTERNAMN = "Pärsson";

  @Before
  public void setup() {
    // Logga in
    String session = createAuthSession(FORNAMN, EFTERNAMN, PERSONNUMMER);
    RestAssured.sessionId = session;

    // Ta bort hosp-info
    spec().when()
        .delete("api/test/hosp/remove/" + PERSONNUMMER);

    // Ta bort registrering
    spec().delete("api/test/registration/remove/" + PERSONNUMMER);

    // Rensa mail-stubbe
    spec().delete("api/stub/mails/clear");
  }

  @Test
  public void testTillbakadragenLakarbehorighet() {
    // Lägg till i hosp
    spec().body(createHospInformation(PERSONNUMMER))
        .expect()
        .statusCode(200)
        .when()
        .post("api/test/hosp/add");

    // Se till att uppdaterat namn finns
    spec().get("api/user");

    // Skapa registrering
    spec().body(createValidRegistration())
        .expect()
        .statusCode(200)
        .when()
        .post("api/registration/create");

    // Verifiera läkarbehörighet
    spec().when()
        .get("api/registration")
        .then()
        .assertThat()
        .body("hospInformation.hsaTitles", Matchers.contains("Läkare"));

    // Logga in genom webcert
    spec().when()
        .post("api/test/webcert/validatePrivatePractitioner/" + PERSONNUMMER)
        .then()
        .assertThat()
        .body("resultCode", Matchers.equalTo("OK"));

    // Ta bort behörighet
    spec().when()
        .delete("api/test/hosp/remove/" + PERSONNUMMER);

    // Trigga hosp-uppdatering
    spec().expect()
        .statusCode(200)
        .when()
        .post("api/test/hosp/update");

    // Logga in med ERROR som resultat
    spec().when()
        .post("api/test/webcert/validatePrivatePractitioner/" + PERSONNUMMER)
        .then()
        .assertThat()
        .body("resultCode", Matchers.equalTo("NOT_AUTHORIZED_IN_HOSP"));
  }

  @Test
  public void testUppdateraTillLakare() {
    // Se till att uppdaterat namn finns
    spec().get("api/user");

    // Skapa registrering
    spec().body(createValidRegistration())
        .expect()
        .statusCode(200)
        .when()
        .post("api/registration/create");

    // Verifiera läkarbehörighet saknas
    spec().when()
        .get("api/registration")
        .then()
        .assertThat()
        .body("hospInformation.hsaTitles", is(Matchers.emptyOrNullString()));

    // Lägg till i hosp
    spec().body(createHospInformation(PERSONNUMMER))
        .expect()
        .statusCode(200)
        .when()
        .post("api/test/hosp/add");

    // Trigga hosp-uppdatering
    spec().expect()
        .statusCode(200)
        .when()
        .post("api/test/hosp/update");

    // Verifiera läkarbehörighet
    spec().when()
        .get("api/registration")
        .then()
        .assertThat()
        .body("hospInformation.hsaTitles", Matchers.contains("Läkare"));

    // Verifiera att mail mottagits i stubbe
    spec().when()
        .get("api/stub/mails")
        .then()
        .assertThat()
        .body(PERSONNUMMER, Matchers.equalTo("Registration klar"));
  }

  @Test
  public void testRemovalStrategy() {
    // Se till att uppdaterat namn finns
    spec().expect()
        .statusCode(200)
        .when().get("api/user");

    // Skapa registrering
    spec().body(createValidRegistration())
        .expect()
        .statusCode(200)
        .when()
        .post("api/registration/create");

    // Ändra registreringsdatum så att städningen ska triggas
    spec().body("2017-01-15")
        .expect()
        .statusCode(200)
        .when()
        .post("api/test/registration/setregistrationdate/" + PERSONNUMMER);

    // Trigga hosp-uppdatering
    spec().expect()
        .statusCode(200)
        .when()
        .post("api/test/hosp/update");

    // Verifiera att mail om borttagen registrering gått iväg
    spec().when()
        .get("api/stub/mails")
        .then()
        .assertThat()
        .body(PERSONNUMMER, Matchers.equalTo("Registrering borttagen"));

    // Försök hämta registreringsinfo, denna ska vara rensad
    spec().when()
        .get("api/test/registration/" + PERSONNUMMER)
        .then()
        .assertThat()
        .body(is(Matchers.emptyOrNullString()));
  }

  private CreateRegistrationRequest createValidRegistration() {

    CreateRegistrationRequest registrationRequest = new CreateRegistrationRequest();
    Registration registration = new Registration();

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

    registrationRequest.setRegistration(registration);
    registrationRequest.setGodkantMedgivandeVersion(1L);
    return registrationRequest;
  }

  private String createHospInformation(String personNummer) {

    JSONArray specialities = new JSONArray();
    JSONObject speciality = new JSONObject();
    speciality.put("specialityCode", "32");
    speciality.put("specialityName", "Klinisk fysiologi");
    specialities.add(speciality);
    speciality = new JSONObject();
    speciality.put("specialityCode", "74");
    speciality.put("specialityName", "Nukleärmedicin");
    specialities.add(speciality);

    JSONArray licences = new JSONArray();
    JSONObject licence = new JSONObject();
    licence.put("healthCareProfessionalLicenceCode", "LK");
    licence.put("healthCareProfessionalLicenceName", "Läkare");
    licences.add(licence);

    JSONObject body = new JSONObject();
    body.put("personalIdentityNumber", personNummer);
    body.put("personalPrescriptionCode", "1234567");
    body.put("educationCodes", Lists.newArrayList());
    body.put("restrictions", Lists.newArrayList());
    body.put("specialities", specialities);
    body.put("healthCareProfessionalLicenceType", licences);
    return body.toJSONString();
  }
}

