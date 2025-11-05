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
package se.inera.intyg.privatepractitionerservice.application.services;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for "REST-ish" integrationTests using RestAssured.
 * <p/>
 * Created by marced on 19/11/15.
 */
public abstract class BaseIntegrationTest {

  /**
   * Common setup for all tests.
   */
  @BeforeEach
  public void setupBase() {
    RestAssured.reset();
    RestAssured.baseURI = System.getProperty("integration.tests.baseUrl", "http://127.0.0.1:8060");
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(
        "application/xml;charset=utf-8").build();
//    RestAssured.config = RestAssured.config()
//        .sessionConfig(RestAssured.config().getSessionConfig().sessionIdName("SESSION"));

  }

  @AfterEach
  public void cleanupBase() {
    RestAssured.reset();
  }

}
