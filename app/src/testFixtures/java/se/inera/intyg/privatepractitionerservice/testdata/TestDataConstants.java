package se.inera.intyg.privatepractitionerservice.testdata;

import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Restriction;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;

public final class TestDataConstants {

  public static final String DR_KRANSTEGE_PERSON_ID = "197705232382";
  public static final String DR_KRANSTEGE_HSA_ID = "SE165565594230-WEBCERTBOOT5";
  public static final String DR_KRANSTEGE_NAME = "Frida Kranstege";
  public static final String DR_KRANSTEGE_POSITION = "201010";
  public static final String DR_KRANSTEGE_CARE_UNIT_NAME = "Kranstegs nukelärmedicin";
  public static final String DR_KRANSTEGE_OWNERSHIP_TYPE = "Privat";
  public static final String DR_KRANSTEGE_TYPE_OF_CARE = "01";
  public static final String DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE = "11";
  public static final String DR_KRANSTEGE_WORKPLACE_CODE = "555";
  public static final String DR_KRANSTEGE_PHONE_NUMBER = "444666555";
  public static final String DR_KRANSTEGE_EMAIL = "Frida@Kranstege.se";
  public static final String DR_KRANSTEGE_ADDRESS = "RIDDARGATAN 3";
  public static final String DR_KRANSTEGE_ZIP_CODE = "11435";
  public static final String DR_KRANSTEGE_CITY = "STOCKHOLM";
  public static final String DR_KRANSTEGE_MUNICIPALITY = "STOCKHOLM";
  public static final String DR_KRANSTEGE_COUNTY = "STOCKHOLM";
  public static final LocalDateTime DR_KRANSTEGE_REGISTRATION_DATE =
      LocalDateTime.of(2025, 12, 8, 14, 30);
  public static final String DR_KRANSTEGE_PRESCRIPTION_CODE = "1234568";
  public static final List<Speciality> DR_KRANSTEGE_SPECIALITIES = List.of(
      new Speciality("3299", "Klinisk fysiologi", "LK"),
      new Speciality("74", "Nukleärmedicin", "LK")
  );
  public static final List<LicensedHealtcareProfession> DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS =
      List.of(new LicensedHealtcareProfession("LK", "Läkare"));

  public static final List<Restriction> DR_KRANSTEGE_RESTRICTIONS = List.of(
      new Restriction(
          "001",
          "Återkallad legitimation",
          "LK"
      )
  );

  public static final String HEALTHCARE_SERVICE_TYPE_CODE_MEDICAL_SERVICE = "11";
  public static final String HEALTHCARE_SERVICE_TYPE_DESCRIPTION_MEDICAL_SERVICE = "Medicinsk verksamhet";

  public static final String POSITION_CODE_SPECIALIST_DOCTOR = "202010";
  public static final String POSITION_DESCRIPTION_SPECIALIST_DOCTOR = "Specialistläkare";

  public static final String TYPE_OF_CARE_CODE_OUTPATIENT = "01";
  public static final String TYPE_OF_CARE_DESCRIPTION_OUTPATIENT = "Öppenvård";

  private TestDataConstants() {
  }
}