package se.inera.intyg.privatepractitionerservice.testdata;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.temporal.ChronoUnit;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

public class TestDataAssert {

  public static void assertPrivatlakareEntity(PrivatlakareEntity expected,
      PrivatlakareEntity actual) {
    assertAll(
        () -> assertEquals(expected.getPrivatlakareId(), actual.getPrivatlakareId()),
        () -> assertEquals(expected.getPersonId(), actual.getPersonId()),
        () -> assertEquals(expected.getHsaId(), actual.getHsaId()),
        () -> assertEquals(expected.getFullstandigtNamn(), actual.getFullstandigtNamn()),
        () -> assertEquals(expected.getForskrivarKod(), actual.getForskrivarKod()),
        () -> assertEquals(expected.isGodkandAnvandare(), actual.isGodkandAnvandare()),
        () -> assertEquals(expected.getEnhetsId(), actual.getEnhetsId()),
        () -> assertEquals(expected.getEnhetsNamn(), actual.getEnhetsNamn()),
        () -> assertEquals(expected.getArbetsplatsKod(), actual.getArbetsplatsKod()),
        () -> assertEquals(expected.getAgarform(), actual.getAgarform()),
        () -> assertEquals(expected.getPostadress(), actual.getPostadress()),
        () -> assertEquals(expected.getPostnummer(), actual.getPostnummer()),
        () -> assertEquals(expected.getPostort(), actual.getPostort()),
        () -> assertEquals(expected.getTelefonnummer(), actual.getTelefonnummer()),
        () -> assertEquals(expected.getEpost(), actual.getEpost()),
        () -> assertEquals(expected.getEnhetStartdatum(), actual.getEnhetStartdatum()),
        () -> assertEquals(expected.getEnhetSlutDatum(), actual.getEnhetSlutDatum()),
        () -> assertEquals(expected.getLan(), actual.getLan()),
        () -> assertEquals(expected.getKommun(), actual.getKommun()),
        () -> assertEquals(expected.getVardgivareId(), actual.getVardgivareId()),
        () -> assertEquals(expected.getVardgivareNamn(), actual.getVardgivareNamn()),
        () -> assertEquals(expected.getVardgivareStartdatum(), actual.getVardgivareStartdatum()),
        () -> assertEquals(expected.getVardgivareSlutdatum(), actual.getVardgivareSlutdatum()),
        () -> assertEquals(expected.getBefattningar(), actual.getBefattningar()),
        () -> assertEquals(expected.getLegitimeradeYrkesgrupper(),
            actual.getLegitimeradeYrkesgrupper()),
        () -> assertEquals(expected.getSpecialiteter(), actual.getSpecialiteter()),
        () -> assertEquals(expected.getVerksamhetstyper(), actual.getVerksamhetstyper()),
        () -> assertEquals(expected.getVardformer(), actual.getVardformer()),
        () -> assertEquals(expected.getSenasteHospUppdatering(),
            actual.getSenasteHospUppdatering()),
        () -> assertTrue(
            Math.abs(ChronoUnit.SECONDS.between(
                expected.getRegistreringsdatum(),
                actual.getRegistreringsdatum()
            )) <= 1,
            "Registreringsdatum should be within 1 second")
    );
  }

  private TestDataAssert() {
    throw new IllegalStateException("Utility class");
  }
}
