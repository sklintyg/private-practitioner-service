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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub.MailStore;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub.OutgoingMail;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "dev")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = MailServiceTestConfig.class)
class MailServiceImplTest {

  @Autowired
  private MailStore mailStore;

  @Autowired
  private MailService mailService;

  @Value("${mail.port}")
  private String port;

  @Value("${mail.username}")
  private String username;

  @Value("${mail.password}")
  private String password;

  @Value("${mail.smtps.auth}")
  private boolean smtpsAuth;

  private PrivatlakareEntity createTestRegistration() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setPostadress("Testadress");
    privatlakareEntity.setAgarform("Testägarform");
    privatlakareEntity.setEpost("test@test.com");
    return privatlakareEntity;
  }

  @Test
  void testMailProperties() {
    assertTrue(!password.isEmpty());
    assertFalse(smtpsAuth);
    assertEquals(25, Integer.parseInt(port));
  }

  @Test
  void testSendMail() {
    PrivatlakareEntity privatlakareEntity = createTestRegistration();
    mailService.sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED,
        privatlakareEntity.getEpost());
    mailStore.waitForMails(1);

    OutgoingMail oneMail = mailStore.getMails().get(0);
    assertEquals(1, mailStore.getMails().size());
    assertEquals("test@test.com", oneMail.getRecipients().get(0));
    assertEquals("Webcert är klar att användas", oneMail.getSubject());
  }

  @AfterEach
  void cleanMailStore() {
    mailStore.getMails().clear();
  }

}
