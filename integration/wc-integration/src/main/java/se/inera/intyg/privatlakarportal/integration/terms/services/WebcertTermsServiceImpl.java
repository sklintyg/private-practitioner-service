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
package se.inera.intyg.privatlakarportal.integration.terms.services;

import jakarta.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.integration.terms.services.dto.Terms;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.AvtalType;

/**
 * Created by pebe on 2015-08-25.
 */
@Service
public class WebcertTermsServiceImpl implements WebcertTermsService {

  private static final Logger LOG = LoggerFactory.getLogger(WebcertTermsService.class);

  @Autowired
  private TermsWebServiceCalls client;

  @Override
  public Terms getTerms() {
    try {
      AvtalType avtalType = client.getPrivatePractitionerTerms().getAvtal();

      if (avtalType == null) {
        LOG.error("getAvtal is null in getPrivatePractitionerTerms");
        throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR,
            "Unable to lookup terms");
      }

      return new Terms(avtalType.getAvtalText(), avtalType.getAvtalVersion(),
          avtalType.getAvtalVersionDatum());
    } catch (WebServiceException e) {
      LOG.error("WebServiceException '{}' in getPrivatePractitionerTerms", e.getMessage());
      throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR,
          "Unable to lookup terms");
    }
  }
}
