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
package se.inera.intyg.privatlakarportal.common.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jakarta.mail.MessagingException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.common.service.stub.MailStore;
import se.inera.intyg.privatlakarportal.common.service.stub.OutgoingMail;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "dev")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = MailServiceTestConfig.class)
public class MailServiceImplTest {

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

  private Privatlakare createTestRegistration() {
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setPostadress("Testadress");
    privatlakare.setAgarform("Testägarform");
    privatlakare.setEpost("test@test.com");
    return privatlakare;
  }

  @Test
  public void testMailProperties() {
    assertTrue(!password.isEmpty());
    assertFalse(smtpsAuth);
    assertEquals(25, Integer.parseInt(port));
  }

  @Test
  public void testSendMail() throws MessagingException {
    Privatlakare privatlakare = createTestRegistration();
    mailService.sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED, privatlakare);
    mailStore.waitForMails(1);

    OutgoingMail oneMail = mailStore.getMails().get(0);
    assertEquals(1, mailStore.getMails().size());
    assertEquals("test@test.com", oneMail.getRecipients().get(0));
    assertEquals("Webcert är klar att användas", oneMail.getSubject());
  }

  @After
  public void cleanMailStore() {
    mailStore.getMails().clear();
  }

}
