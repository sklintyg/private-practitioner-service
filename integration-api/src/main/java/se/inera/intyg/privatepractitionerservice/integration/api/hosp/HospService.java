package se.inera.intyg.privatepractitionerservice.integration.api.hosp;

import java.time.LocalDateTime;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

public interface HospService {

  HospCredentialsForPerson getHospCredentialsForPersonResponseType(String personalIdentityNumber);

  LocalDateTime getHospLastUpdate();

  Result handleHospCertificationPersonResponseType(
      String certificationId, String operation, String personalIdentityNumber, String reason);

}
