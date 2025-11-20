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
      final var personDTO = PersonDTO.builder()
          .personnummer("197705232382")
          .fornamn("Frida")
          .mellannamn("Stege")
          .efternamn("Kranstege")
          .build();
      final var result = converter.convertPerson(personDTO);
      assertEquals("Frida Stege Kranstege", result.getName());
    }

    @Test
    void shouldConvertPersonWithoutMiddleName() {
      final var personDTO = PersonDTO.builder()
          .personnummer("197705232382")
          .fornamn("Frida")
          .mellannamn(null)
          .efternamn("Kranstege")
          .build();
      final var result = converter.convertPerson(personDTO);
      assertEquals("Frida Kranstege", result.getName());
    }

    @Test
    void shouldConvertPersonId() {
      final var personDTO = PersonDTO.builder()
          .personnummer("197705232382")
          .fornamn("Frida")
          .efternamn("Kranstege")
          .build();
      final var result = converter.convertPerson(personDTO);
      assertEquals("197705232382", result.getPersonId());
    }

    @Test
    void shouldReturnPersonObject() {
      final var personDTO = PersonDTO.builder()
          .personnummer("197705232382")
          .fornamn("Frida")
          .efternamn("Kranstege")
          .build();
      final var result = converter.convertPerson(personDTO);
      assertEquals(Person.class, result.getClass());
    }
  }
}