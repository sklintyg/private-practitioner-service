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
package se.inera.intyg.privatlakarportal.integration.config;

// CHECKSTYLE:OFF LineLength

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import se.inera.intyg.privatlakarportal.integration.privatepractitioner.services.GetPrivatePractitionerResponderImpl;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitioner.v1.rivtabp21.GetPrivatePractitionerResponderInterface;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerterms.v1.rivtabp21.GetPrivatePractitionerTermsResponderInterface;

// CHECKSTYLE:ON LineLength

@Configuration
@ComponentScan({
    "se.inera.intyg.privatlakarportal.integration.privatepractitioner",
    "se.inera.intyg.privatlakarportal.integration.terms"})
@ImportResource("classpath:wc-services.xml")
public class WcIntegrationConfiguration {

  @Value("${terms.ws.services.url}")
  private String termsWsUrl;

  @Autowired
  private Bus cxfBus;

  @Bean
  public GetPrivatePractitionerResponderInterface getPrivatePractitionerResponder() {
    return new GetPrivatePractitionerResponderImpl();
  }

  @Bean
  public EndpointImpl getPrivatePractitionerEndpoint() {
    Object implementor = getPrivatePractitionerResponder();
    EndpointImpl endpoint = new EndpointImpl(cxfBus, implementor);
    endpoint.publish("/get-private-practitioner/v1.0");
    return endpoint;
  }

  @Bean
  public GetPrivatePractitionerTermsResponderInterface termsWebServiceClient() {
    JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
    proxyFactoryBean.setAddress(termsWsUrl);
    proxyFactoryBean.setServiceClass(GetPrivatePractitionerTermsResponderInterface.class);
    return (GetPrivatePractitionerTermsResponderInterface) proxyFactoryBean.create();
  }
}
