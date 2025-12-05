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
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_RESTRICTIONS;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_SPECIALITIES;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_TYPE_OF_CARE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_WORKPLACE_CODE;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_ZIP_CODE;

import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.BefattningEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.RestriktionEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VardformEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VerksamhetstypEntity;

public class TestDataEntities {

  public static final PrivatlakareEntity DR_KRANSTEGE_ENTITY = kranstegeEntityBuilder().build();

  public static PrivatlakareEntity.PrivatlakareEntityBuilder kranstegeEntityBuilder() {
    return PrivatlakareEntity.builder()
        .personId(DR_KRANSTEGE_PERSON_ID)
        .hsaId(DR_KRANSTEGE_HSA_ID)
        .enhetsId(DR_KRANSTEGE_HSA_ID)
        .vardgivareId(DR_KRANSTEGE_HSA_ID)
        .fullstandigtNamn(DR_KRANSTEGE_NAME)
        .vardgivareNamn(DR_KRANSTEGE_CARE_UNIT_NAME)
        .enhetsNamn(DR_KRANSTEGE_CARE_UNIT_NAME)
        .agarform(DR_KRANSTEGE_OWNERSHIP_TYPE)
        .arbetsplatsKod(DR_KRANSTEGE_WORKPLACE_CODE)
        .telefonnummer(DR_KRANSTEGE_PHONE_NUMBER)
        .epost(DR_KRANSTEGE_EMAIL)
        .postadress(DR_KRANSTEGE_ADDRESS)
        .postnummer(DR_KRANSTEGE_ZIP_CODE)
        .postort(DR_KRANSTEGE_CITY)
        .kommun(DR_KRANSTEGE_MUNICIPALITY)
        .lan(DR_KRANSTEGE_COUNTY)
        .forskrivarKod(DR_KRANSTEGE_PRESCRIPTION_CODE)
        .verksamhetstyper(
            List.of(
                VerksamhetstypEntity.builder()
                    .kod(DR_KRANSTEGE_HEALTHCARE_SERVICE_TYPE)
                    .build()
            )
        )
        .vardformer(
            List.of(
                VardformEntity.builder()
                    .kod(DR_KRANSTEGE_TYPE_OF_CARE)
                    .build()
            )
        )
        .registreringsdatum(LocalDateTime.now())
        .befattningar(
            List.of(
                BefattningEntity.builder()
                    .kod(DR_KRANSTEGE_POSITION)
                    .build()
            )
        )
        .specialiteter(
            DR_KRANSTEGE_SPECIALITIES.stream()
                .map(speciality -> SpecialitetEntity.builder()
                    .kod(speciality.code())
                    .namn(speciality.name())
                    .build()
                )
                .toList()
        )
        .legitimeradeYrkesgrupper(
            DR_KRANSTEGE_LICENSED_HEALTHCARE_PROFESSIONS.stream()
                .map(profession -> LegitimeradYrkesgruppEntity.builder()
                    .kod(profession.code())
                    .namn(profession.name())
                    .build()
                )
                .toList()
        )
        .restriktioner(DR_KRANSTEGE_RESTRICTIONS.stream()
            .map(restriction -> RestriktionEntity.builder()
                .kod(restriction.code())
                .namn(restriction.name())
                .build())
            .toList())
        .godkandAnvandare(true);
  }

  private TestDataEntities() {
    throw new IllegalStateException("Utility class");
  }
}
