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

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import se.inera.intyg.privatlakarportal.integration.privatepractitioner.services.util.RestUtil;

public abstract class BaseRestIntegrationTest {

  @Before
  public void setupBase() {
    RestAssured.reset();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    RestAssured.baseURI = System.getProperty("integration.tests.baseUrl", "http://localhost:8060");
    RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON)
        .build();
    RestAssured.config = newConfig().sslConfig(sslConfig().allowAllHostnames())
        .sessionConfig(RestAssured.config().getSessionConfig().sessionIdName("SESSION"));
  }

  @After
  public void cleanupBase() {
    RestAssured.reset();
  }

  protected static String createAuthSession(String firstName, String lastName, String personId) {
    String sessionId = RestUtil.login(firstName, lastName, personId);
    RestAssured.sessionId = sessionId;
    return sessionId;
  }


  void sleep(long milllis) {
    try {
      Thread.sleep(milllis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  RequestSpecification spec() {
    return given()
        .contentType(ContentType.JSON)
        .cookie("ROUTEID", RestUtil.routeId);
  }

  RequestSpecification spec(long delayInMillis) {
    sleep(delayInMillis);
    return spec();
  }


}
