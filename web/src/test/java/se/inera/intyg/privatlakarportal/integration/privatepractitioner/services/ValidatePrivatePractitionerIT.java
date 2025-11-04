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

import static org.hamcrest.core.Is.is;

import org.junit.Test;
import se.inera.intyg.privatepractitioner.dto.ValidatePrivatePractitionerResultCode;

public class ValidatePrivatePractitionerIT extends BaseRestIntegrationTest {

  private static final String VALIDATE_PRIVATE_PRACTITIONER = "api/test/webcert/validatePrivatePractitioner/";
  private static final String RESULT_CODE = "resultCode";
  private static final String PERSONAL_IDENTITY_NUMBER = "191212121212";
  private static final String PERSONAL_IDENTITY_NUMBER_UNKNOWN = "UNKNOWN";

  @Test
  public void testValidatePrivatePractitioner() {
    spec()
        .expect()
        .statusCode(200)
        .body(RESULT_CODE, is(ValidatePrivatePractitionerResultCode.OK.name()))
        .when()
        .post(VALIDATE_PRIVATE_PRACTITIONER + PERSONAL_IDENTITY_NUMBER);
  }

  @Test
  public void testValidatePrivatePractitionerThatIsNotValid() {
    spec()
        .expect()
        .statusCode(200)
        .body(RESULT_CODE, is(ValidatePrivatePractitionerResultCode.NO_ACCOUNT.name()))
        .when()
        .post(VALIDATE_PRIVATE_PRACTITIONER + PERSONAL_IDENTITY_NUMBER_UNKNOWN);
  }

}
