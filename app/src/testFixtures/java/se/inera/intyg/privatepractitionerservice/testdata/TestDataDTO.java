package se.inera.intyg.privatepractitionerservice.testdata;

import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.CONSENT_FORM_VERSION;
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
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_WORKPLACE_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ZIP_CODE;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO.PrivatePractitionerDTOBuilder;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson.HospCredentialsForPersonBuilder;

public class TestDataDTO {

  public static final CreateRegistrationRequest DR_KRANSTEGE_REQUEST = kranstegeRegistrationRequest().build();
  public static final PrivatePractitionerDTO DR_KRANSTEGE_DTO = kranstegeDTOBuilder().build();
  public static final HospCredentialsForPerson DR_KRANSTEGE_HOSP_CREDENTIALS = kranstegeHospCredentialsBuilder().build();

  public static PrivatePractitionerDTOBuilder kranstegeDTOBuilder() {
    return PrivatePractitionerDTO.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .hsaId(DR_KRANSTEGE_HSA_ID)
        .name(DR_KRANSTEGE_NAME)
        .email(DR_KRANSTEGE_EMAIL)
        .careProviderName(DR_KRANSTEGE_CARE_UNIT_NAME);
  }

  public static CreateRegistrationRequest.CreateRegistrationRequestBuilder kranstegeRegistrationRequest() {
    return CreateRegistrationRequest.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .name(DR_KRANSTEGE_NAME)
        .position(DR_KRANSTEGE_POSITION)
        .careUnitName(DR_KRANSTEGE_CARE_UNIT_NAME)
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
        .consentFormVersion(CONSENT_FORM_VERSION);
  }

  public static HospCredentialsForPersonBuilder kranstegeHospCredentialsBuilder() {
    return HospCredentialsForPerson.builder()
        .personalIdentityNumber(DR_KRANSTEGE_PERSON_ID)
        .personalPrescriptionCode(DR_KRANSTEGE_PRESCRIPTION_CODE)
        .healthCareProfessionalLicenceSpeciality(
            DR_KRANSTEGE_SPECIALITIES.stream()
                .map(speciality -> HCPSpecialityCodes.builder()
                    .specialityCode(speciality.code())
                    .specialityName(speciality.name())
                    .build()
                )
                .toList()
        )
        .healthCareProfessionalLicence(
            DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS.stream()
                .map(licensedHealtcareProfession -> HealthCareProfessionalLicence.builder()
                    .healthCareProfessionalLicenceCode(licensedHealtcareProfession.code())
                    .healthCareProfessionalLicenceName(licensedHealtcareProfession.name())
                    .build()
                )
                .toList()
        );
  }
}
