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

import java.util.Properties;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import se.inera.intyg.privatlakarportal.common.service.MailService;
import se.inera.intyg.privatlakarportal.common.service.MailServiceImpl;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = {"file:${dev.config.file}"})
@EnableAsync
public class MailServiceConfig implements AsyncConfigurer {

  @Value("${mail.host}")
  private String mailHost;

  @Value("${mail.protocol}")
  private String protocol;

  @Value("${mail.username}")
  private String username;

  @Value("${mail.password}")
  private String password;

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

  private static final Logger LOG = LoggerFactory.getLogger(MailServiceConfig.class);

  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(mailHost);
    mailSender.setDefaultEncoding(defaultEncoding);
    mailSender.setProtocol(protocol);
    mailSender.setPort(Integer.parseInt(port));
    if (username != null && !username.isEmpty()) {
      mailSender.setUsername(username);
    }
    if (password != null && !password.isEmpty()) {
      mailSender.setPassword(password);
    }

    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail." + protocol + ".auth", smtpsAuth);
    javaMailProperties.put("mail." + protocol + ".port", Integer.parseInt(port));
    javaMailProperties.put("mail." + protocol + ".starttls.enable", smtpsStarttlsEnable);
    javaMailProperties.put("mail." + protocol + ".debug", smtpsDebug);
    javaMailProperties.put("mail." + protocol + ".socketFactory.fallback", true);

    mailSender.setJavaMailProperties(javaMailProperties);
    LOG.info("Mailsender initialized with: [port: {}, protocol: {}, host: {}]", port, protocol,
        mailHost);
    return mailSender;
  }

  @Bean
  public MailService mailService() {
    return new MailServiceImpl();
  }

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // CHECKSTYLE:OFF MagicNumber
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(42);
    executor.setQueueCapacity(100);
    // CHECKSTYLE:ON MagicNumber
    executor.setThreadNamePrefix("MyExecutor-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }
}
