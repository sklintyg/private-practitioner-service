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
package se.inera.intyg.privatlakarportal.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import se.inera.intyg.privatlakarportal.common.service.MailService;
import se.inera.intyg.privatlakarportal.common.service.stub.MailServiceStub;
import se.inera.intyg.privatlakarportal.common.service.stub.MailStubStore;

@Configuration
@Profile({"mail-stub"})
@PropertySource("classpath:application.properties")
public class MailServiceStubConfig {

  @Bean
  public MailService mailService() {
    return new MailServiceStub();
  }

  @Bean
  public JavaMailSender mailSender() {
    return new JavaMailSenderImpl();
  }

  @Bean
  MailStubStore mailstore() {
    return new MailStubStore();
  }
}
