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
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.integration.terms.services.dto.Terms;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionertermsresponder.v1.GetPrivatePractitionerTermsResponseType;
import se.riv.infrastructure.directory.privatepractitioner.terms.v1.AvtalType;

/**
 * Created by pebe on 2015-08-25.
 */
@RunWith(MockitoJUnitRunner.class)
public class WebcertTermServiceTest {

  @Mock
  private TermsWebServiceCalls client;

  @InjectMocks
  private WebcertTermsServiceImpl webcertTermsService;

  @Test
  public void getTerms() {
    AvtalType avtalType = new AvtalType();
    avtalType.setAvtalText("TestText");
    avtalType.setAvtalVersion(42);
    GetPrivatePractitionerTermsResponseType getPrivatePractitionerTermsResponseType = new GetPrivatePractitionerTermsResponseType();
    getPrivatePractitionerTermsResponseType.setAvtal(avtalType);
    when(client.getPrivatePractitionerTerms()).thenReturn(getPrivatePractitionerTermsResponseType);

    Terms terms = webcertTermsService.getTerms();
    assertEquals("TestText", terms.getText());
    assertEquals(42, terms.getVersion());
  }

  @Test(expected = PrivatlakarportalServiceException.class)
  public void getTermsNull() {
    GetPrivatePractitionerTermsResponseType getPrivatePractitionerTermsResponseType = new GetPrivatePractitionerTermsResponseType();
    when(client.getPrivatePractitionerTerms()).thenReturn(getPrivatePractitionerTermsResponseType);

    Terms terms = webcertTermsService.getTerms();
    assertEquals("TestText", terms.getText());
    assertEquals(42, terms.getVersion());
  }
}
