package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

class PersonSvarConverterTest {

  private PersonSvarConverter converter;

  private static final String PERSON_ID = "197705232382";
  public static final String KRANSTEGE_FIRST_NAME = "Frida";
  public static final String KRANSTEGE_LAST_NAME = "Kranstege";

  @BeforeEach
  void setUp() {
    converter = new PersonSvarConverter();
  }

  @Nested
  class ConvertStatus {

    @Test
    void shouldConvertFoundStatus() {
      final var result = converter.convertStatus(StatusDTO.FOUND);
      assertEquals(Status.FOUND, result);
    }

    @Test
    void shouldConvertNotFoundStatus() {
      final var result = converter.convertStatus(StatusDTO.NOT_FOUND);
      assertEquals(Status.NOT_FOUND, result);
    }

    @Test
    void shouldConvertErrorStatus() {
      final var result = converter.convertStatus(StatusDTO.ERROR);
      assertEquals(Status.ERROR, result);
    }

    @Test
    void shouldReturnStatusObject() {
      final var result = converter.convertStatus(StatusDTO.FOUND);
      assertEquals(Status.class, result.getClass());
    }
  }

  @Nested
  class ConvertPerson {

    @Test
    void shouldConvertPersonWithAllNameParts() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals("Frida Kranstege", result.getName());
    }

    @Test
    void shouldConvertPersonWithMissingLastName() {
      final var personDTO = new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, null);
      final var result = converter.convertPerson(personDTO);
      assertEquals(KRANSTEGE_FIRST_NAME, result.getName());
    }

    @Test
    void shouldConvertPersonId() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals(PERSON_ID, result.getPersonId());
    }

    @Test
    void shouldReturnPersonObject() {
      final var personDTO = getPersonDTO();
      final var result = converter.convertPerson(personDTO);
      assertEquals(Person.class, result.getClass());
    }

    private static PersonDTO getPersonDTO() {
      return new PersonDTO(PERSON_ID, KRANSTEGE_FIRST_NAME, KRANSTEGE_LAST_NAME);
    }
  }
}