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
package se.inera.intyg.privatlakarportal.config;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import se.inera.intyg.infra.security.common.cookie.IneraCookieSerializer;
import se.inera.intyg.infra.security.filter.InternalApiFilter;
import se.inera.intyg.infra.security.filter.PrincipalUpdatedFilter;

@Configuration
@EnableTransactionManagement
@PropertySource(ignoreResourceNotFound = true,
    value = {"classpath:application.properties", "file:${dev.config.file}",
        "classpath:version.properties"})
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
@ComponentScan({"se.inera.intyg.infra.integration.intygproxyservice",
    "se.inera.intyg.privatlakarportal.logging",
    "se.inera.intyg.infra.pu.integration.intygproxyservice"})
public class ApplicationConfig {

  @Autowired
  private Bus cxfBus;

  @Bean
  public CookieSerializer cookieSerializer() {
        /*
        This is needed to make IdP functionality work.
        This will not satisfy all browsers, but it works for IE, Chrome and Edge.
        Reference: https://auth0.com/blog/browser-behavior-changes-what-developers-need-to-know/
         */
    return new IneraCookieSerializer();
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource source = new ResourceBundleMessageSource();
    source.setBasename("version");
    source.setUseCodeAsDefaultMessage(true);
    return source;
  }

  @PostConstruct
  public Bus cxfBus() {
    cxfBus.setFeatures(
        new ArrayList<>(Arrays.asList(loggingFeature())));

    return cxfBus;
  }

  @Bean
  public LoggingFeature loggingFeature() {
    LoggingFeature loggingFeature = new LoggingFeature();
    loggingFeature.setPrettyLogging(true);
    return loggingFeature;
  }

  @Bean
  public PrincipalUpdatedFilter principalUpdatedFilter() {
    return new PrincipalUpdatedFilter();
  }

  @Bean
  public InternalApiFilter internalApiFilter() {
    return new InternalApiFilter();
  }

}
