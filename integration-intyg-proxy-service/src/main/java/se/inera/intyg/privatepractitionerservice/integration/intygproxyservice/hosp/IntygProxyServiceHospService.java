/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
public class IntygProxyServiceHospService implements
    HospService {

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
