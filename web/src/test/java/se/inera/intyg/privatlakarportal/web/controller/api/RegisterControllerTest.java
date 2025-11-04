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
package se.inera.intyg.privatlakarportal.web.controller.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.postnummer.model.Omrade;
import se.inera.intyg.infra.integration.postnummer.service.PostnummerService;
import se.inera.intyg.privatlakarportal.common.model.Registration;
import se.inera.intyg.privatlakarportal.service.RegisterService;
import se.inera.intyg.privatlakarportal.service.model.HospInformation;
import se.inera.intyg.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.intyg.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.CreateRegistrationRequest;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.SaveRegistrationRequest;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

  @Mock
  private RegisterService registerService;

  @Mock
  private PostnummerService postnummerService;

  @InjectMocks
  private RegisterController registerController = new RegisterController();

  @Test
  public void testGetRegistration() {
    var registrationWithHospInformation = new RegistrationWithHospInformation(new Registration(),
        new HospInformation(), true);
    when(registerService.getRegistration()).thenReturn(registrationWithHospInformation);

    var getRegistrationResponse = registerController.getRegistration();

    verify(registerService).getRegistration();
    assertTrue(getRegistrationResponse.isWebcertUserTermsApproved());
  }

  @Test
  public void testCreateRegistration() {
    CreateRegistrationRequest request = new CreateRegistrationRequest();
    request.setGodkantMedgivandeVersion(1L);
    registerController.createRegistration(request);

    verify(registerService).createRegistration(request.getRegistration(), 1L);
  }

  @Test
  public void testCreateRegistrationSave() {
    when(registerService.saveRegistration(any())).thenReturn(SaveRegistrationResponseStatus.OK);

    SaveRegistrationRequest request = new SaveRegistrationRequest();
    request.setRegistration(new Registration());
    var saveRegistrationResponse = registerController.createRegistration(request);

    verify(registerService).saveRegistration(request.getRegistration());
    assertEquals(SaveRegistrationResponseStatus.OK, saveRegistrationResponse.getStatus());

  }

  @Test
  public void testGetHospInformation() {
    when(registerService.getHospInformation()).thenReturn(new HospInformation());

    var getHospInformationResponse = registerController.getHospInformation();

    verify(registerService).getHospInformation();
    assertNotNull(getHospInformationResponse.getHospInformation());
  }

  @Test
  public void testGetOmrade() {
    var zipCode = "Zip code";
    var omrade = new Omrade("", "", "", "");
    when(postnummerService.getOmradeByPostnummer(anyString())).thenReturn(
        Collections.singletonList(omrade));

    var getOmradeResponse = registerController.getOmrade(zipCode);

    verify(postnummerService).getOmradeByPostnummer(zipCode);
    assertEquals(1, getOmradeResponse.getOmradeList().size());
  }
}
