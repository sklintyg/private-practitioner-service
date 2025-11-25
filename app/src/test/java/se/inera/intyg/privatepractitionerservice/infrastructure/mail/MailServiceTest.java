package se.inera.intyg.privatepractitionerservice.infrastructure.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataEntities.DR_KRANSTEGE_ENTITY;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.ADMIN_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.FROM_EMAIL;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.HSA_GENERATION_MAIL_SUBJECT;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.REGISTRATION_APPROVED_MAIL_SUBJECT;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.REGISTRATION_PENDING_MAIL_SUBJECT;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.REGISTRATION_REJECTED_MAIL_SUBJECT;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataMail.REGISTRATION_REMOVED_MAIL_SUBJECT;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @InjectMocks
  private MailService mailService;

  @Nested
  class TestSendHsaGenerationEmail {

    @Mock
    private MimeMessage message;

    @BeforeEach
    void setUp() {
      setField(mailService, "adminEpost", ADMIN_EMAIL);
      setField(mailService, "from", FROM_EMAIL);
      setField(mailService, "hsaGenerationMailSubject", HSA_GENERATION_MAIL_SUBJECT);

      when(mailSender.createMimeMessage()).thenReturn(message);
    }

    @Test
    void shouldSendFromNoReply() throws MessagingException {
      mailService.sendHsaGenerationEmail();

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).setFrom(captor.capture());
      assertEquals(FROM_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendToAdmin() throws MessagingException {
      mailService.sendHsaGenerationEmail();

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).addRecipient(eq(Message.RecipientType.TO), captor.capture());
      assertEquals(ADMIN_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendWithSubject() throws MessagingException {
      mailService.sendHsaGenerationEmail();

      final var captor = ArgumentCaptor.forClass(String.class);
      verify(message).setSubject(captor.capture(), eq("UTF-8"));
      assertEquals(HSA_GENERATION_MAIL_SUBJECT, captor.getValue());
    }
  }

  @Nested
  class TestSendRegistrationStatusEmail {

    @Mock
    private MimeMessage message;

    @BeforeEach
    void setUp() {
      setField(mailService, "from", FROM_EMAIL);
      setField(mailService, "approvedSubject", REGISTRATION_APPROVED_MAIL_SUBJECT);
      setField(mailService, "notApprovedSubject", REGISTRATION_REJECTED_MAIL_SUBJECT);
      setField(mailService, "awaitingHospSubject", REGISTRATION_PENDING_MAIL_SUBJECT);

      when(mailSender.createMimeMessage()).thenReturn(message);
    }

    @Test
    void shouldSendFromNoReply() throws MessagingException {
      mailService.sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED, DR_KRANSTEGE_EMAIL);

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).setFrom(captor.capture());
      assertEquals(FROM_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendToPrivatePractitioner() throws MessagingException {
      mailService.sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED, DR_KRANSTEGE_EMAIL);

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).addRecipient(eq(Message.RecipientType.TO), captor.capture());
      assertEquals(DR_KRANSTEGE_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendWithSubjectApprovedWhenAuthorized() throws MessagingException {
      mailService.sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED, DR_KRANSTEGE_EMAIL);

      final var captor = ArgumentCaptor.forClass(String.class);
      verify(message).setSubject(captor.capture(), eq("UTF-8"));
      assertEquals(REGISTRATION_APPROVED_MAIL_SUBJECT, captor.getValue());
    }

    @Test
    void shouldSendWithSubjectRejectedWhenAuthorized() throws MessagingException {
      mailService.sendRegistrationStatusEmail(RegistrationStatus.NOT_AUTHORIZED,
          DR_KRANSTEGE_EMAIL);

      final var captor = ArgumentCaptor.forClass(String.class);
      verify(message).setSubject(captor.capture(), eq("UTF-8"));
      assertEquals(REGISTRATION_REJECTED_MAIL_SUBJECT, captor.getValue());
    }

    @Test
    void shouldSendWithSubjectPendingWhenWaitingForHosp() throws MessagingException {
      mailService.sendRegistrationStatusEmail(RegistrationStatus.WAITING_FOR_HOSP,
          DR_KRANSTEGE_EMAIL);

      final var captor = ArgumentCaptor.forClass(String.class);
      verify(message).setSubject(captor.capture(), eq("UTF-8"));
      assertEquals(REGISTRATION_PENDING_MAIL_SUBJECT, captor.getValue());
    }
  }

  @Nested
  class TestSendRegistrationRemovedEmail {

    @Mock
    private MimeMessage message;

    @BeforeEach
    void setUp() {
      setField(mailService, "from", FROM_EMAIL);
      setField(mailService, "registrationRemovedSubject", REGISTRATION_REMOVED_MAIL_SUBJECT);

      when(mailSender.createMimeMessage()).thenReturn(message);
    }

    @Test
    void shouldSendFromNoReply() throws MessagingException {
      mailService.sendRegistrationRemovedEmail(DR_KRANSTEGE_ENTITY);

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).setFrom(captor.capture());
      assertEquals(FROM_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendToPrivatePractitioner() throws MessagingException {
      mailService.sendRegistrationRemovedEmail(DR_KRANSTEGE_ENTITY);

      final var captor = ArgumentCaptor.forClass(InternetAddress.class);
      verify(message).addRecipient(eq(Message.RecipientType.TO), captor.capture());
      assertEquals(DR_KRANSTEGE_EMAIL, captor.getValue().getAddress());
    }

    @Test
    void shouldSendWithSubject() throws MessagingException {
      mailService.sendRegistrationRemovedEmail(DR_KRANSTEGE_ENTITY);

      final var captor = ArgumentCaptor.forClass(String.class);
      verify(message).setSubject(captor.capture(), eq("UTF-8"));
      assertEquals(REGISTRATION_REMOVED_MAIL_SUBJECT, captor.getValue());
    }
  }
}