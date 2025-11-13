package se.inera.intyg.privatepractitionerservice.testdata;

import java.util.List;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;

public final class TestDataConstants {

  public static final String DR_KRANSTEGE_PERSON_ID = "197705232382";
  public static final String DR_KRANSTEGE_HSA_ID = "SE165565594230-WEBCERT00001";
  public static final String DR_KRANSTEGE_NAME = "Frida Kranstege";
  public static final String DR_KRANSTEGE_POSITION = "201010";
  public static final String DR_KRANSTEGE_CARE_UNIT_NAME = "Kransteges specialistmottagning";
  public static final String DR_KRANSTEGE_OWNERSHIP_TYPE = "Privat";
  public static final String DR_KRANSTEGE_TYPE_OF_CARE = "01";
  public static final String DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE = "15";
  public static final String DR_KRANSTEGE_WORKPLACE_CODE = "555";
  public static final String DR_KRANSTEGE_PHONE_NUMBER = "0123-456789";
  public static final String DR_KRANSTEGE_EMAIL = "frida@kranstege.se";
  public static final String DR_KRANSTEGE_ADDRESS = "Addressgatan 1";
  public static final String DR_KRANSTEGE_ZIP_CODE = "12345";
  public static final String DR_KRANSTEGE_CITY = "Stad";
  public static final String DR_KRANSTEGE_MUNICIPALITY = "Kommun";
  public static final String DR_KRANSTEGE_COUNTY = "Län";
  public static final String DR_KRANSTEGE_PRESCRIPTION_CODE = "12345";
  public static final List<Speciality> DR_KRANSTEGE_SPECIALITIES = List.of(
      new Speciality("32", "Klinisk fysiologi"),
      new Speciality("74", "Nukleärmedicin")
  );
  public static final List<LicensedHealtcareProfession> DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS =
      List.of(
          new LicensedHealtcareProfession("LK", "Läkare")
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