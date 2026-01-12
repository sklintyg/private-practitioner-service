package se.inera.intyg.privatepractitionerservice.testdata;

public class TestDataMail {

  private TestDataMail() {
    throw new IllegalStateException("Utility class");
  }

  public static final String ADMIN_EMAIL = "admin@webcert.se";
  public static final String FROM_EMAIL = "no-reply@webcert.intygstjanster.se";

  public static final String HSA_GENERATION_MAIL_SUBJECT = "För-TAK-ade HSA-id för Privatläkarportalen börjar ta slut.";
  public static final String HSA_GENERATION_MAIL_BODY = "Det är dags att TAK:a nytt. Det har totalt skapats %d konton i tjänsten";

  public static final String REGISTRATION_APPROVED_MAIL_SUBJECT = "Webcert är klar att användas";
  public static final String REGISTRATION_APPROVED_MAIL_BODY = "Dina uppgifter avseende giltig läkarlegitimation har hämtats från Socialstyrelsen och du kan nu börja använda Webcert.";
  public static final String REGISTRATION_PENDING_MAIL_SUBJECT = "Registrering för Webcert";
  public static final String REGISTRATION_PENDING_MAIL_BODY = "Dina uppgifter avseende giltig läkarlegitimation har fortfarande inte kunnat hämtats från Socialstyrelsen. Du bör kontakta Socialstyrelsen för att verifiera att dina legitimationsuppgifter är korrekta.";
  public static final String REGISTRATION_REMOVED_MAIL_SUBJECT = "Registrering för Webcert";
}
