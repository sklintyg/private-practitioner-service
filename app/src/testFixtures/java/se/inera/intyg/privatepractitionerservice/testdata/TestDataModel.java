package se.inera.intyg.privatepractitionerservice.testdata;

import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ADDRESS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_CARE_UNIT_NAME;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_CITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_COUNTY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_MUNICIPALITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_NAME;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_OWNERSHIP_TYPE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PHONE_NUMBER;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_POSITION;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PRESCRIPTION_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_REGISTRATION_DATE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_RESTRICTIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_WORKPLACE_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ZIP_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.HEALTHCARE_SERVICE_TYPE_CODE_MEDICAL_SERVICE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.HEALTHCARE_SERVICE_TYPE_DESCRIPTION_MEDICAL_SERVICE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.POSITION_CODE_SPECIALIST_DOCTOR;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.POSITION_DESCRIPTION_SPECIALIST_DOCTOR;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.TYPE_OF_CARE_CODE_OUTPATIENT;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.TYPE_OF_CARE_DESCRIPTION_OUTPATIENT;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HealthcareServiceType;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Position;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.TypeOfCare;

public class TestDataModel {

  public static final PrivatePractitioner DR_KRANSTEGE = kranstegeBuilder().build();
  public static final HospPerson DR_KRANSTEGE_HOSP_PERSON = kranstegeHospPersonBuilder().build();

  public static final HealthcareServiceType HEALTHCARE_SERVICE_TYPE_MEDICAL_SERVICE =
      new HealthcareServiceType(
          HEALTHCARE_SERVICE_TYPE_CODE_MEDICAL_SERVICE,
          HEALTHCARE_SERVICE_TYPE_DESCRIPTION_MEDICAL_SERVICE
      );

  public static final Position POSITION_SPECIALIST_DOCTOR = new Position(
      POSITION_CODE_SPECIALIST_DOCTOR,
      POSITION_DESCRIPTION_SPECIALIST_DOCTOR
  );

  public static final TypeOfCare TYPE_OF_CARE_OUTPATIENT = new TypeOfCare(
      TYPE_OF_CARE_CODE_OUTPATIENT,
      TYPE_OF_CARE_DESCRIPTION_OUTPATIENT
  );

  public static PrivatePractitioner.PrivatePractitionerBuilder kranstegeBuilder() {
    return PrivatePractitioner.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .hsaId(DR_KRANSTEGE_HSA_ID)
        .name(DR_KRANSTEGE_NAME)
        .position(DR_KRANSTEGE_POSITION)
        .careUnitName(DR_KRANSTEGE_CARE_UNIT_NAME)
        .careProviderName(DR_KRANSTEGE_CARE_UNIT_NAME)
        .ownershipType(DR_KRANSTEGE_OWNERSHIP_TYPE)
        .typeOfCare(DR_KRANSTEGE_TYPE_OF_CARE)
        .healthcareServiceType(DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE)
        .workplaceCode(DR_KRANSTEGE_WORKPLACE_CODE)
        .phoneNumber(DR_KRANSTEGE_PHONE_NUMBER)
        .email(DR_KRANSTEGE_EMAIL)
        .address(DR_KRANSTEGE_ADDRESS)
        .zipCode(DR_KRANSTEGE_ZIP_CODE)
        .city(DR_KRANSTEGE_CITY)
        .municipality(DR_KRANSTEGE_MUNICIPALITY)
        .county(DR_KRANSTEGE_COUNTY)
        .registrationDate(DR_KRANSTEGE_REGISTRATION_DATE)
        .specialties(DR_KRANSTEGE_SPECIALITIES)
        .licensedHealthcareProfessions(DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS)
        .personalPrescriptionCode(DR_KRANSTEGE_PRESCRIPTION_CODE);
  }

  public static HospPerson.HospPersonBuilder kranstegeHospPersonBuilder() {
    return HospPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .personalPrescriptionCode(DR_KRANSTEGE_PRESCRIPTION_CODE)
        .licensedHealthcareProfessions(DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS)
        .specialities(DR_KRANSTEGE_SPECIALITIES)
        .restrictions(DR_KRANSTEGE_RESTRICTIONS);
  }

  private TestDataModel() {
    throw new IllegalStateException("Utility class");
  }
}
