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
package se.inera.intyg.privatlakarportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.hsa.services.HospPersonService;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.monitoring.MonitoringLogService;

@Service
public class EraseServiceImpl implements EraseService {

  private static final Logger LOG = LoggerFactory.getLogger(EraseServiceImpl.class);

  @Value("${erase.private.practitioner:true}")
  private boolean erasePrivatePractitioner;

  private final PrivatlakareRepository privatlakareRepository;
  private final HospPersonService hospPersonService;
  private final MonitoringLogService monitoringLogService;

  public EraseServiceImpl(PrivatlakareRepository privatlakareRepository,
      HospPersonService hospPersonService,
      MonitoringLogService monitoringLogService) {
    this.privatlakareRepository = privatlakareRepository;
    this.hospPersonService = hospPersonService;
    this.monitoringLogService = monitoringLogService;
  }

  @Override
  @Transactional
  public void erasePrivatePractitioner(String careProviderId) {
    final var privatePractitioner = privatlakareRepository.findByHsaId(careProviderId);

    if (privatePractitioner == null) {
      LOG.warn("Could not find private practitioner with hsa-id {}. Nothing was erased.",
          careProviderId);
      return;
    }

    if (!erasePrivatePractitioner) {
      LOG.warn(
          "Erase private practitioner is inactivated via configuration. Private practitioner with hsa-id {} was not erased.",
          careProviderId);
      return;
    }

    if (hospPersonService.removeFromCertifier(privatePractitioner.getPersonId(), null,
        "Avslutat konto i Webcert.")) {
      privatlakareRepository.delete(privatePractitioner);
      monitoringLogService.logUserErased(privatePractitioner.getPersonId(), careProviderId);
      LOG.info("Erased private practitioner with hsa-id {}.", careProviderId);
    } else {
      LOG.error("Failure unregistering private practitioner {} in certifier branch.",
          privatePractitioner.getHsaId());
      throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR,
          "Failure unregistering private practitioner in certifier branch.");
    }
  }
}
