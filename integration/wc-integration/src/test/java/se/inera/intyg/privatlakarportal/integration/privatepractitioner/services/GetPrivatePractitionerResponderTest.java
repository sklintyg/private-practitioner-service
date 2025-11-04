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
package se.inera.intyg.privatlakarportal.integration.privatepractitioner.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.riv.infrastructure.directory.privatepractitioner.getprivatepractitionerresponder.v1.GetPrivatePractitionerType;

/**
 * Created by pebe on 2015-08-19.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetPrivatePractitionerResponderTest {

  @Mock
  private IntegrationService integrationService;

  @InjectMocks
  private GetPrivatePractitionerResponderImpl getPrivatePractitionerResponder = new GetPrivatePractitionerResponderImpl();

  @Test(expected = IllegalArgumentException.class)
  public void testGetBothIds() {
    GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
    getPrivatePractitionerType.setPersonalIdentityNumber("id1");
    getPrivatePractitionerType.setPersonHsaId("id2");
    getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNoIds() {
    GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
    getPrivatePractitionerType.setPersonalIdentityNumber(null);
    getPrivatePractitionerType.setPersonHsaId(null);
    getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
  }

  @Test
  public void testGetPersonId() {
    GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
    getPrivatePractitionerType.setPersonalIdentityNumber("id1");
    getPrivatePractitionerType.setPersonHsaId(null);
    getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
    verify(integrationService).getPrivatePractitionerByPersonId("id1");
    verifyNoMoreInteractions(integrationService);
  }

  @Test
  public void testGetHsaId() {
    GetPrivatePractitionerType getPrivatePractitionerType = new GetPrivatePractitionerType();
    getPrivatePractitionerType.setPersonalIdentityNumber(null);
    getPrivatePractitionerType.setPersonHsaId("id2");
    getPrivatePractitionerResponder.getPrivatePractitioner("", getPrivatePractitionerType);
    verify(integrationService).getPrivatePractitionerByHsaId("id2");
    verifyNoMoreInteractions(integrationService);
  }
}
