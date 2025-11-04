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
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.ResultCodeEnum;

/**
 * Created by eriklupander on 2016-09-02.
 */
public class GetPrivatePractitionerIT extends BaseIntegrationTest {

  private static final String BASE = "Envelope.Body.GetPrivatePractitionerResponse.";
  private static final String GET_PRIVATE_PRACTITIONER_V1_0 = "services/get-private-practitioner/v1.0";
  private static final String PNR = "191212121212";
  public static final String PNR_OKANT = "inte-ett-personnummer-i-varje-fall";

  private ST requestTemplate;
  private ST brokenRequest;
  private STGroup templateGroup;


  @Before
  public void setup() throws IOException {
    // Setup String template resource
    templateGroup = new STGroupFile("integrationtestTemplates/getPrivatePractitioner.v1.stg");
    requestTemplate = templateGroup.getInstanceOf("request");
    brokenRequest = templateGroup.getInstanceOf("brokenrequest");
  }

  @Test
  public void testGetPrivatePractitioner() throws Exception {
    requestTemplate.add("data", new GetData(PNR));
    given().body(requestTemplate.render())
        .when()
        .post(GET_PRIVATE_PRACTITIONER_V1_0)
        .then().statusCode(200)
        .rootPath(BASE)
        .body("resultCode", is(ResultCodeEnum.OK.value()))
        .body("hoSPerson.hsaId.@extension", is("SE165565594230-WEBCERTBOOT1"))
        .body("hoSPerson.personId.@extension", is("191212121212"))
        .body("hoSPerson.enhet.enhetsnamn", is("TestEnhet"));
  }

  @Test
  public void testGetPrivatePractitionerThatDoesNotExist() throws Exception {
    requestTemplate.add("data", new GetData(PNR_OKANT));
    given().body(requestTemplate.render())
        .when()
        .post(GET_PRIVATE_PRACTITIONER_V1_0)
        .then().statusCode(200)
        .rootPath(BASE)
        .body("resultCode", is(ResultCodeEnum.ERROR.value()));
  }

  @Test
  public void testGetPrivatePractitionerWithInvalidRequest() throws Exception {
    given().body(brokenRequest.render())
        .when()
        .post(GET_PRIVATE_PRACTITIONER_V1_0)
        .then().statusCode(500)
        .rootPath(BASE);
  }

  private class GetData {

    public final String personId;

    public GetData(String personId) {
      this.personId = personId;
    }
  }

}
