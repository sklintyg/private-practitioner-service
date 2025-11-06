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
package se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;

@Service
public class MailServiceStub implements MailService {

  private static final String AUTHORIZED_BODY = "Registration klar";
  private static final String NOT_AUTHORIZED_BODY = "Saknar behörighet";
  private static final String WAITING_FOR_HOSP_BODY = "Väntar på hosp-information";
  private static final String HSA_GENERATION_STATUS = "Dags att göra ny TAKning för genererade HSA-IDn";
  private static final Logger LOG = LoggerFactory.getLogger(MailServiceStub.class);
  private static final String REGISTRATION_REMOVED = "Registrering borttagen";

  @Value("{mail.admin}")
  private String adminEpost;

  @Autowired(required = false)
  private MailStubStore mailStore;

  @Override
  public void sendHsaGenerationStatusEmail() {
    LOG.info("Sending registration status email to {}", adminEpost);
    try {
      mailStore.addMail("ADMIN", HSA_GENERATION_STATUS);
    } catch (PrivatlakarportalServiceException e) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  @Override
  public void sendRegistrationStatusEmail(RegistrationStatus status,
      PrivatlakareEntity privatlakareEntity)
      throws PrivatlakarportalServiceException {
    LOG.info("Sending registration status email to {}", privatlakareEntity.getEpost());
    try {
      mailStore.addMail(privatlakareEntity.getPersonId(), createHtmlBody(status));
    } catch (MessagingException | PrivatlakarportalServiceException e) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  @Override
  public void sendRegistrationRemovedEmail(PrivatlakareEntity privatlakareEntity) {
    LOG.info("Sending registration removed email to {}", privatlakareEntity.getEpost());
    try {
      mailStore.addMail(privatlakareEntity.getPersonId(), REGISTRATION_REMOVED);
    } catch (PrivatlakarportalServiceException e) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  private String createHtmlBody(RegistrationStatus status)
      throws MessagingException, PrivatlakarportalServiceException {
    String htmlString = null;

    switch (status) {
      case AUTHORIZED:
        htmlString = AUTHORIZED_BODY;
        break;
      case NOT_AUTHORIZED:
        htmlString = NOT_AUTHORIZED_BODY;
        break;
      case NOT_STARTED:
        break;
      case WAITING_FOR_HOSP:
        htmlString = WAITING_FOR_HOSP_BODY;
        break;
    }
    return htmlString;
  }

}
