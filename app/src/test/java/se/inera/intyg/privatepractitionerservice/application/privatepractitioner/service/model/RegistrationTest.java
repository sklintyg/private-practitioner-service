package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RegistrationTest {

  @Test
  void testRegistrationIsValid() {
    Registration registration = new Registration();
    registration.setAdress("Testadress");
    registration.setAgarForm("Testagarform");
    registration.setArbetsplatskod("Kod");
    registration.setBefattning("Befattning");
    registration.setEpost("test@test.se");
    registration.setKommun("Kommun");
    registration.setLan("Ln");
    registration.setPostnummer("12345");
    registration.setPostort("postort");
    registration.setTelefonnummer("12343455");
    registration.setVardform("Vrdform");
    registration.setVerksamhetensNamn("Verksamhetsnamn");
    registration.setVerksamhetstyp("Typ");

    assertTrue(registration.checkIsValid());
  }

}
