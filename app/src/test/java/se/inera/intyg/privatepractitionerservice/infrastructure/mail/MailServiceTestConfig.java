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

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub.JavaMailSenderAroundAdvice;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.stub.MailStore;

@Configuration
@Profile("dev")
@PropertySource({"classpath:MailServiceTest/test.properties"})
@EnableAspectJAutoProxy
public class MailServiceTestConfig {

  @Value("${mail.host}")
  private String mailHost;

  @Value("${mail.protocol}")
  private String protocol;

  @Value("${mail.username}")
  private String username;

  @Value("${mail.password}")
  private String password;

  @Value("${mail.from}")
  private String from;

  @Value("${mail.defaultEncoding}")
  private String defaultEncoding;

  @Value("${mail.port}")
  private String port;

  @Value("${mail.smtps.auth}")
  private boolean smtpsAuth;

  @Value("${mail.smtps.starttls.enable}")
  private boolean smtpsStarttlsEnable;

  @Value("${mail.smtps.debug}")
  private boolean smtpsDebug;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public MailService mailService() {
    return new MailServiceImpl();
  }

  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    int intPort = Integer.parseInt(port);
    mailSender.setHost(mailHost);
    mailSender.setDefaultEncoding(defaultEncoding);
    mailSender.setProtocol(protocol);
    mailSender.setPort(intPort);

    if (!username.isEmpty()) {
      mailSender.setUsername(username);
    }
    if (!password.isEmpty()) {
      mailSender.setPassword(password);
    }

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail." + protocol + ".port", intPort);
    javaMailProperties.put("mail." + protocol + ".auth", smtpsAuth);
    javaMailProperties.put("mail." + protocol + ".starttls.enable", smtpsStarttlsEnable);
    javaMailProperties.put("mail." + protocol + ".debug", smtpsDebug);
    javaMailProperties.put("mail." + protocol + ".socketFactory.fallback", true);
    mailSender.setJavaMailProperties(javaMailProperties);
    return mailSender;
  }

  @Bean
  JavaMailSenderAroundAdvice javaMailSenderAroundAdvice() {
    return new JavaMailSenderAroundAdvice();
  }

  @Bean
  MailStore mailstore() {
    return new MailStore();

  }
}
