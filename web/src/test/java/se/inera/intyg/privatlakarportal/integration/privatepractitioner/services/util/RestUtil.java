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
package se.inera.intyg.privatlakarportal.integration.privatepractitioner.services.util;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Created by stillor on 2/3/17.
 */
public final class RestUtil {

  public static String routeId;

  private RestUtil() {
  }

  public static String login(String firstName, String lastName, String personId) {
    JSONObject jsonBody = new JSONObject();
    jsonBody.put("firstName", firstName);
    jsonBody.put("lastName", lastName);
    jsonBody.put("personId", personId);

    Map<String, String> cookies =
        given()
            .contentType(ContentType.URLENC)
            .body("userJsonDisplay=" + jsonBody.toJSONString())
            .redirects().follow(false)
            .expect().statusCode(HttpServletResponse.SC_FOUND)
            .when().post("/fake").getCookies();

    routeId = cookies.containsKey("ROUTEID") ? cookies.get("ROUTEID") : "nah";
    return cookies.get("SESSION");
  }

}
