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
package se.inera.intyg.privatepractitionerservice.infrastructure.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@Service
public class MailServiceImpl implements MailService {

  @Value("${mail.admin}")
  private String adminEpost;

  @Value("${mail.from}")
  private String from;

  @Value("${mail.content.approved.body}")
  private String approvedBody;

  @Value("${mail.content.rejected.body}")
  private String notApprovedBody;

  @Value("${mail.content.pending.body}")
  private String awaitingHospBody;

  @Value("${mail.content.approved.subject}")
  private String approvedSubject;

  @Value("${mail.content.rejected.subject}")
  private String notApprovedSubject;

  @Value("${mail.content.pending.subject}")
  private String awaitingHospSubject;

  @Value("${mail.admin.content.hsa.subject}")
  private String hsaGenerationMailSubject;

  @Value("${mail.admin.content.hsa.body}")
  private String hsaGenerationMailBody;

  @Value("${mail.content.removed.body}")
  private String registrationRemovedBody;

  @Value("${mail.content.removed.subject}")
  private String registrationRemovedSubject;

  private static final String INERA_LOGO = "inera_logo.png";

  private static final String BOTTOM_BODY_CONTENT = "<br/><br/><span><img src='cid:inera_logo'></span>";

  private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

  @Autowired
  private JavaMailSender mailSender;

  @Override
  @Async
  public void sendHsaGenerationStatusEmail() {
    try {
      LOG.info("Sending hsa-generation-status email to {}", adminEpost);
      MimeMessage message = createMessage(adminEpost, hsaGenerationMailSubject,
          hsaGenerationMailBody);
      message.saveChanges();
      mailSender.send(message);
    } catch (MessagingException | PrivatlakarportalServiceException | MailException |
             IOException e) {
      LOG.error("Error while sending registration status email with message {}", e.getMessage(), e);
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  @Override
  @Async
  public void sendRegistrationStatusEmail(RegistrationStatus status, String epost) {
    try {
      LOG.info("Sending registration status email to {}", epost);
      final var message = createMessage(epost,
          messageSubjectFromRegistrationStatus(status),
          messageBodyFromRegistrationStatus(status)
      );
      message.saveChanges();
      mailSender.send(message);
    } catch (MessagingException | PrivatlakarportalServiceException | MailException |
             IOException e) {
      LOG.error("Error while sending registration status email with message {}", e.getMessage(), e);
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  @Override
  @Async
  public void sendRegistrationRemovedEmail(PrivatlakareEntity privatlakareEntity) {
    try {
      LOG.info("Sending registration status email to {}", privatlakareEntity.getEpost());
      MimeMessage message = createMessage(privatlakareEntity.getEpost(), registrationRemovedSubject,
          registrationRemovedBody);
      message.saveChanges();
      mailSender.send(message);
    } catch (MessagingException | PrivatlakarportalServiceException | MailException |
             IOException e) {
      LOG.error("Error while sending registration status email with message {}", e.getMessage(), e);
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, e.getMessage());
    }
  }

  private MimeMessage createMessage(String email, String subject, String body)
      throws MessagingException, IOException {
    MimeMessage message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(from));
    message.addRecipient(Message.RecipientType.TO,
        new InternetAddress(email));
    buildEmailContent(message, subject, body);
    return message;
  }

  private String messageSubjectFromRegistrationStatus(RegistrationStatus status) {
    String htmlSubjectString = null;
    // Set correct subject depending on status
    switch (status) {
      case AUTHORIZED:
        htmlSubjectString = approvedSubject;
        break;
      case NOT_AUTHORIZED:
        htmlSubjectString = notApprovedSubject;
        break;
      case NOT_STARTED:
        // Ignored
        break;
      case WAITING_FOR_HOSP:
        htmlSubjectString = awaitingHospSubject;
        break;
    }
    return htmlSubjectString;
  }

  private String messageBodyFromRegistrationStatus(RegistrationStatus status) {
    String htmlBodyString = null;

    // Set correct content depending on status
    switch (status) {
      case AUTHORIZED:
        htmlBodyString = approvedBody;
        break;
      case NOT_AUTHORIZED:
        htmlBodyString = notApprovedBody;
        break;
      case NOT_STARTED:
        // Ignored
        break;
      case WAITING_FOR_HOSP:
        htmlBodyString = awaitingHospBody;
        break;
    }
    return htmlBodyString;
  }

  private void buildEmailContent(MimeMessage message, String subjectText, String bodyText)
      throws MessagingException, IOException {
    String htmlBodyString = bodyText;
    htmlBodyString += BOTTOM_BODY_CONTENT;

    // Use mimeHelper to set content
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subjectText);
    helper.setText(htmlBodyString, true);
    final InputStreamSource imageSource = new ByteArrayResource(getLogo());
    helper.addInline("inera_logo", imageSource, "image/png");
  }

  private byte[] getLogo() throws IOException {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(INERA_LOGO);
    byte[] bytes = IOUtils.toByteArray(is);
    LOG.debug("Getting file as bytes");
    return bytes;
  }
}
