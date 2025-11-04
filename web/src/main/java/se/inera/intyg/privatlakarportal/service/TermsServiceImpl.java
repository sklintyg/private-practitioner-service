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

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.integration.terms.services.dto.Terms;
import se.inera.intyg.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.intyg.privatlakarportal.persistence.repository.MedgivandeTextRepository;

@Service
public class TermsServiceImpl implements TermsService {

  private static final Logger LOG = LoggerFactory.getLogger(TermsServiceImpl.class);

  @Autowired
  private MedgivandeTextRepository medgivandeTextRepository;

  @Autowired
  private SubscriptionService subscriptionService;

  @Value("${webcert.terms.approved.url}")
  private String termsApprovedUrl;

  private RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    restTemplate = new RestTemplate();
  }

  @Override
  public Terms getTerms() {
    MedgivandeText medgivandeText = medgivandeTextRepository.findLatest();
    if (medgivandeText == null) {
      LOG.error("getTerms: Could not find medgivandetext");
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Could not find medgivandetext");
    }
    return new Terms(medgivandeText.getMedgivandeText(), medgivandeText.getVersion(),
        medgivandeText.getDatum());
  }

  @Override
  public Boolean getWebcertUserTermsApproved(String hsaId) {
    if (onlyFetchUserTermsIfSubscriptionIsNotRequired()) {
      try {
        final var response = restTemplate.getForEntity(termsApprovedUrl + "/" + hsaId,
            Boolean.class);
        if (response.hasBody() && response.getStatusCode() == HttpStatus.OK) {
          return response.getBody();
        }
      } catch (RestClientException e) {
        LOG.error("Failed call to Webcert for approved terms information", e);
        return false;
      }
    }
    return false;
  }

  private boolean onlyFetchUserTermsIfSubscriptionIsNotRequired() {
    return !subscriptionService.isSubscriptionRequired();
  }

}
