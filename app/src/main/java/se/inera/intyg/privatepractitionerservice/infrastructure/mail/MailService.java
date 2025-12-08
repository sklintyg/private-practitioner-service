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

import static se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

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

  private static final String BOTTOM_BODY_CONTENT = "<br/><br/><br/><span><img src='cid:inera_logo' style='max-width: 75%%; max-height: auto'></span>";

  private final JavaMailSender mailSender;

  @Async
  public void sendHsaGenerationEmail() {
    try {
      log.info("Sending hsa-generation-status email to {}", adminEpost);
      final var message = message(adminEpost, hsaGenerationMailSubject, hsaGenerationMailBody);
      message.saveChanges();
      mailSender.send(message);
    } catch (Exception ex) {
      log.error("Error while sending hsa-generation-status email!", ex);
      throw new PrivatlakarportalServiceException(UNKNOWN_INTERNAL_PROBLEM, ex.getMessage());
    }
  }

  @Async
  public void sendRegistrationStatusEmail(RegistrationStatus status, String epost) {
    try {
      log.info("Sending registration status email to {}", epost);
      final var message = message(epost,
          messageSubjectFromRegistrationStatus(status),
          messageBodyFromRegistrationStatus(status)
      );
      message.saveChanges();
      mailSender.send(message);
    } catch (Exception ex) {
      log.error("Error while sending registration status email!", ex);
      throw new PrivatlakarportalServiceException(UNKNOWN_INTERNAL_PROBLEM, ex.getMessage());
    }
  }

  @Async
  public void sendRegistrationRemovedEmail(PrivatlakareEntity privatlakareEntity) {
    sendRegistrationRemovedEmail(privatlakareEntity.getEpost());
  }

  public void sendRegistrationRemovedEmail(String email) {
    try {
      log.info("Sending registration removed email to {}", email);
      final var message = message(email, registrationRemovedSubject,
          registrationRemovedBody);
      message.saveChanges();
      mailSender.send(message);
    } catch (Exception ex) {
      log.error("Error while sending registration removed email {}", ex);
      throw new PrivatlakarportalServiceException(UNKNOWN_INTERNAL_PROBLEM, ex.getMessage());
    }
  }

  private MimeMessage message(String email, String subject, String body)
      throws MessagingException, IOException {
    final var message = mailSender.createMimeMessage();
    message.setFrom(new InternetAddress(from));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
    buildEmailContent(message, subject, body);
    return message;
  }

  private String messageSubjectFromRegistrationStatus(RegistrationStatus status) {
    return switch (status) {
      case AUTHORIZED -> approvedSubject;
      case NOT_AUTHORIZED -> notApprovedSubject;
      case WAITING_FOR_HOSP -> awaitingHospSubject;
    };
  }

  private String messageBodyFromRegistrationStatus(RegistrationStatus status) {
    return switch (status) {
      case AUTHORIZED -> approvedBody;
      case NOT_AUTHORIZED -> notApprovedBody;
      case WAITING_FOR_HOSP -> awaitingHospBody;
    };
  }

  private void buildEmailContent(MimeMessage message, String subjectText, String bodyText)
      throws MessagingException, IOException {
    final var helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setSubject(subjectText);
    helper.setText(bodyText + BOTTOM_BODY_CONTENT, true);
    final InputStreamSource imageSource = new ByteArrayResource(getLogo());
    helper.addInline("inera_logo", imageSource, "image/png");
  }

  private byte[] getLogo() throws IOException {
    final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(INERA_LOGO);
    if (is == null) {
      throw new IOException("Could not find resource: " + INERA_LOGO);
    }
    return IOUtils.toByteArray(is);
  }
}
