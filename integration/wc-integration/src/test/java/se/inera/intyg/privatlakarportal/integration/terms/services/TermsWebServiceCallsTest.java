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
package se.inera.intyg.privatlakarportal.integration.terms.services;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.inera.intyg.privatlakarportal.integration.terms.services.config.TermsWebServiceTestConfig;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "dev")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = TermsWebServiceTestConfig.class)
public class TermsWebServiceCallsTest {

  private static final int AVTAL_VERSION = 1;
  private static final String AVTAL_VERSION_DATUM = "2015-09-30T00:00:00.000";

  @Autowired
  private TermsWebServiceCalls testTermsWS;

  @Test
  public void testTerms() {
    GetPrivatePractitionerTermsResponseType response = testTermsWS.getPrivatePractitionerTerms();
    assertEquals(LocalDateTime.parse(AVTAL_VERSION_DATUM),
        response.getAvtal().getAvtalVersionDatum());
    assertEquals(AVTAL_VERSION, response.getAvtal().getAvtalVersion());
  }

}
