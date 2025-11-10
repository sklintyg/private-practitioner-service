package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter;

import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospInformation;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;

public class PrivatePractitionerConverter {

  public static HospInformation convertHospPersonToHospInformation(HospPerson hospPerson) {
    final var hospInfo = new HospInformation();
    hospInfo.setHsaTitles(hospPerson.getHsaTitles());
    hospInfo.setSpecialityNames(hospPerson.getSpecialityNames());
    hospInfo.setPersonalPrescriptionCode(hospPerson.getPersonalPrescriptionCode());
    return hospInfo;
  }

}
