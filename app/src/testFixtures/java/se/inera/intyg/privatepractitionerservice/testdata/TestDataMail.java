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
package se.inera.intyg.privatepractitionerservice.testdata;

public class TestDataMail {

  private TestDataMail() {
    throw new IllegalStateException("Utility class");
  }

  public static final String ADMIN_EMAIL = "admin@webcert.se";
  public static final String FROM_EMAIL = "no-reply@webcert.intygstjanster.se";

  public static final String HSA_GENERATION_MAIL_SUBJECT =
      "För-TAK-ade HSA-id för Privatläkarportalen börjar ta slut.";
  public static final String HSA_GENERATION_MAIL_BODY =
      "Det är dags att TAK:a nytt. Det har totalt skapats %d konton i tjänsten";

  public static final String REGISTRATION_APPROVED_MAIL_SUBJECT = "Webcert är klar att användas";
  public static final String REGISTRATION_APPROVED_MAIL_BODY =
      "Dina uppgifter avseende giltig läkarlegitimation har hämtats från Socialstyrelsen och du kan nu börja använda Webcert.";
  public static final String REGISTRATION_PENDING_MAIL_SUBJECT = "Registrering för Webcert";
  public static final String REGISTRATION_PENDING_MAIL_BODY =
      "Dina uppgifter avseende giltig läkarlegitimation har fortfarande inte kunnat hämtats från Socialstyrelsen. Du bör kontakta Socialstyrelsen för att verifiera att dina legitimationsuppgifter är korrekta.";
  public static final String REGISTRATION_REMOVED_MAIL_SUBJECT = "Registrering för Webcert";
}
