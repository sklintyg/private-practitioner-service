package se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;
import se.inera.intyg.privatepractitionerservice.integration.intygproxyservice.hosp.client.dto.GetHospCertificationPersonRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntygProxyServiceHospService implements HospService {

  private final GetHospCertificationPersonService getHospCertificationPersonService;
  private final GetHospLastUpdateService getHospLastUpdateService;
  private final GetHospCredentialsForPersonService getHospCredentialsForPersonService;

  @Override
  public HospCredentialsForPerson getHospCredentialsForPersonResponseType(
      String personalIdentityNumber) {
    final var hospCredentialsForPerson = getHospCredentialsForPersonService.get(
        personalIdentityNumber);

    if (hospCredentialsForPerson == null) {
      throw new IllegalStateException("Response message did not contain proper response data.");
    }

    return hospCredentialsForPerson;
  }

  @Override
  public LocalDateTime getHospLastUpdate() {
    try {
      return getHospLastUpdateService.get();
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
  }

  @Override
  public Result handleHospCertificationPersonResponseType(String certificationId, String operation,
      String personalIdentityNumber,
      String reason) {
    return getHospCertificationPersonService.get(
        GetHospCertificationPersonRequestDTO.builder()
            .personId(personalIdentityNumber)
            .certificationId(certificationId)
            .operation(operation)
            .reason(reason)
            .build()
    );
  }
}
