package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@Component
public class PersonSvarConverter {

  private static final String SPACE = " ";
  public static final String EMPTY = "";

  public Status convertStatus(StatusDTO statusDTO) {
    return switch (statusDTO) {
      case FOUND -> Status.FOUND;
      case NOT_FOUND -> Status.NOT_FOUND;
      case ERROR -> Status.ERROR;
    };
  }

  public Person convertPerson(PersonDTO personDTO) {
    return Person.builder()
        .personId(personDTO.personnummer())
        .name(buildPersonName(personDTO))
        .build();
  }

  private String buildPersonName(PersonDTO personDTO) {

    return personDTO.fornamn() + getEfternamn(personDTO);
  }

  private String getEfternamn(PersonDTO personDTO) {
    if (personDTO.efternamn() != null && !personDTO.efternamn().isEmpty()) {
      return SPACE + personDTO.efternamn();
    }
    return EMPTY;
  }
}
