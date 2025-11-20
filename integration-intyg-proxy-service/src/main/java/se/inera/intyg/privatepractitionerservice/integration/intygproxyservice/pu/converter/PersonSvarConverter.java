package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Person;
import se.inera.intyg.privatepractitionerservice.integration.api.pu.model.Status;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.PersonDTO;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.pu.client.dto.StatusDTO;

@Component
public class PersonSvarConverter {

  private static final String SPACE = " ";
  private static final String EMPTY = "";

  public Status convertStatus(StatusDTO statusDTO) {
    return switch (statusDTO) {
      case FOUND -> Status.FOUND;
      case NOT_FOUND -> Status.NOT_FOUND;
      case ERROR -> Status.ERROR;
    };
  }

  public Person convertPerson(PersonDTO personDTO) {
    return Person.builder()
        .personId(personDTO.getPersonnummer())
        .name(buildPersonName(personDTO))
        .build();
  }

  private String buildPersonName(PersonDTO personDTO) {
    return personDTO.getFornamn()
        + SPACE
        + includeMiddleName(personDTO.getMellannamn())
        + personDTO.getEfternamn();
  }

  private String includeMiddleName(String middleName) {
    return middleName != null ? middleName + SPACE : EMPTY;
  }
}
