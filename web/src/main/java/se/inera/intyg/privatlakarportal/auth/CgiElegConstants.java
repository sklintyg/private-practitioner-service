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
package se.inera.intyg.privatlakarportal.auth;

import java.util.List;

public class CgiElegConstants {

  private CgiElegConstants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String PERSON_ID_ATTRIBUTE = "Subject_SerialNumber";
  public static final String FORNAMN_ATTRIBUTE = "Subject_GivenName";
  public static final String MELLAN_OCH_EFTERNAMN_ATTRIBUTE = "Subject_Surname";
  public static final String UTFARDARE_ORGANISATIONSNAMN_ATTRIBUTE = "Issuer_OrganizationName";
  public static final String UTFARDARE_CA_NAMN_ATTRIBUTE = "Issuer_CommonName";
  public static final String SECURITY_LEVEL_ATTRIBUTE = "SecurityLevelDescription";
  public static final String RELYING_PARTY_REGISTRATION_ID = "eleg";
  public static final List<String> ELEG_AUTHN_CLASSES = List.of(
      "urn:oasis:names:tc:SAML:2.0:ac:classes:SoftwarePKI",
      "urn:oasis:names:tc:SAML:2.0:ac:classes:SmartcardPKI",
      "urn:oasis:names:tc:SAML:2.0:ac:classes:MobileTwofactorContract",
      "http://id.elegnamnden.se/loa/1.0/loa3");

}
