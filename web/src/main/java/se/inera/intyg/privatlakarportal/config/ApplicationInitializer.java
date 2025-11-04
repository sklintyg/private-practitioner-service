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

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.monitoring.logging.LogbackConfiguratorContextListener;
import se.inera.intyg.infra.security.filter.RequestContextHolderUpdateFilter;
import se.inera.intyg.infra.security.filter.SecurityHeadersFilter;
import se.inera.intyg.privatlakarportal.common.config.MailServiceConfig;
import se.inera.intyg.privatlakarportal.hsa.config.HsaConfiguration;
import se.inera.intyg.privatlakarportal.hsa.config.JobConfiguration;
import se.inera.intyg.privatlakarportal.integration.config.WcIntegrationConfiguration;
import se.inera.intyg.privatlakarportal.persistence.config.PersistenceConfig;
import se.inera.intyg.privatlakarportal.persistence.config.PersistenceConfigDev;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        servletContext.setInitParameter("logbackConfigParameter", "logback.file");
        servletContext.addListener(new LogbackConfiguratorContextListener());

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(WebSecurityConfig.class, ApplicationConfig.class,
            PersistenceConfigDev.class, MailServiceConfig.class,
            HsaConfiguration.class, JobConfiguration.class, PuConfiguration.class,
            CacheConfigurationFromInfra.class,
            WcIntegrationConfiguration.class, ServiceConfig.class, DynamicLinkConfig.class,
            PostnummerserviceConfig.class,
            PersistenceConfig.class, PersistenceConfigDev.class, MonitoringConfiguration.class);

        servletContext.addListener(new ContextLoaderListener(appContext));

        AnnotationConfigWebApplicationContext webConfig = new AnnotationConfigWebApplicationContext();
        webConfig.register(WebConfig.class);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher",
            new DispatcherServlet(webConfig));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        // Spring session filter
        FilterRegistration.Dynamic springSessionRepositoryFilter = servletContext.addFilter(
            "springSessionRepositoryFilter",
            DelegatingFilterProxy.class);
        springSessionRepositoryFilter.addMappingForUrlPatterns(null, false, "/*");

        // Update RequestContext with spring session
        FilterRegistration.Dynamic requestContextHolderUpdateFilter = servletContext.addFilter(
            "requestContextHolderUpdateFilter",
            RequestContextHolderUpdateFilter.class);
        requestContextHolderUpdateFilter.addMappingForUrlPatterns(null, false, "/*");

        // LogMDCServletFilter
        FilterRegistration.Dynamic logMdcFilter = servletContext.addFilter("mdcServletFilter",
            DelegatingFilterProxy.class);
        logMdcFilter.addMappingForUrlPatterns(null, false, "/*");

        // Spring security filter
        FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter(
            "springSecurityFilterChain",
            DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

        // InternalApi filter
        FilterRegistration.Dynamic internalApiFilter = servletContext.addFilter("internalApiFilter",
            DelegatingFilterProxy.class);
        internalApiFilter.addMappingForUrlPatterns(null, false, "/internalapi/*");

        // principalUpdatedFilter filter
        FilterRegistration.Dynamic principalUpdatedFilter = servletContext.addFilter(
            "principalUpdatedFilter",
            DelegatingFilterProxy.class);
        principalUpdatedFilter.setInitParameter("targetFilterLifecycle", "true");
        principalUpdatedFilter.addMappingForUrlPatterns(null, false, "/*");

        // Hidden method filter.
        FilterRegistration.Dynamic hiddenHttpMethodFilter = servletContext.addFilter(
            "hiddenHttpMethodFilter",
            HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(null, false, "/*");

        registerCharachterEncodingFilter(servletContext);

        registerSecurityHeadersFilter(servletContext);

        // Listeners for session audit logging
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new RequestContextListener());
    }

    private void registerCharachterEncodingFilter(ServletContext aContext) {
        CharacterEncodingFilter cef = new CharacterEncodingFilter();
        cef.setForceEncoding(true);
        cef.setEncoding("UTF-8");
        aContext.addFilter("characterEncodingFilter", cef).addMappingForUrlPatterns(null, true, "/*");
    }

    private void registerSecurityHeadersFilter(ServletContext servletContext) {
        SecurityHeadersFilter filter = new SecurityHeadersFilter();
        servletContext.addFilter("securityHeadersFilter", filter)
            .addMappingForUrlPatterns(null, true, "/*");
    }

}
