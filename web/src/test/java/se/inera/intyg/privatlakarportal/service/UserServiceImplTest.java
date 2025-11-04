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
package se.inera.intyg.privatlakarportal.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import se.inera.intyg.infra.pu.integration.api.model.Person;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.infra.pu.integration.api.services.PUService;
import se.inera.intyg.privatlakarportal.auth.PrivatlakarUser;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.logging.HashUtility;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.model.User;
import se.inera.intyg.schemas.contract.Personnummer;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

  private static final String PERSON_ID = "191212121212";
  public static final String PERSON_ID_INVALID = "191212121214";

  private Personnummer personnummer;

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @Mock
  private PUService puService;

  @Mock
  private HashUtility hashUtility;

  @InjectMocks
  private UserServiceImpl userService;

  private Privatlakare privatlakareAuthorized, privatlakareNoHosp, privatlakareNotAuthorized, privatlakareInvalidPnr;

  @Before
  public void setupUser() throws IOException {
    SecurityContextHolder.setContext(getSecurityContext(PERSON_ID, "Test User"));
    privatlakareAuthorized = new CustomObjectMapper().readValue(
        new ClassPathResource("UserServiceImplTest/test_authorized.json").getFile(),
        Privatlakare.class);
    privatlakareNotAuthorized = new CustomObjectMapper().readValue(
        new ClassPathResource("UserServiceImplTest/test_not_authorized.json").getFile(),
        Privatlakare.class);
    privatlakareNoHosp = new CustomObjectMapper().readValue(
        new ClassPathResource("UserServiceImplTest/test_no_hosp.json").getFile(),
        Privatlakare.class);
    privatlakareInvalidPnr = new CustomObjectMapper().readValue(
        new ClassPathResource("UserServiceImplTest/test_invalid_pnr.json").getFile(),
        Privatlakare.class);

    personnummer = createPnr(PERSON_ID);
  }

  @Test
  public void testGetUser() {
    PrivatlakarUser user = userService.getUser();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
  }

  @Test
  public void testGetUserNoRegistration() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
    when(puService.getPerson(personnummer))
        .thenReturn(PersonSvar.found(
            new Person(personnummer, false, false, "Test", "", "User", "", "", "")));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
    assertTrue(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserNotAuthorized() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareNotAuthorized);
    when(puService.getPerson(personnummer))
        .thenReturn(PersonSvar.found(
            new Person(personnummer, false, false, "Test", "", "User", "", "", "")));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.NOT_AUTHORIZED, user.getStatus());
    assertTrue(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserNoHosp() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareNoHosp);
    when(puService.getPerson(personnummer))
        .thenReturn(PersonSvar.found(
            new Person(personnummer, false, false, "Test", "", "User", "", "", "")));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.WAITING_FOR_HOSP, user.getStatus());
    assertTrue(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserRegistration() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareAuthorized);
    when(puService.getPerson(personnummer))
        .thenReturn(PersonSvar.found(
            new Person(personnummer, false, false, "Test", "", "User", "", "", "")));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.AUTHORIZED, user.getStatus());
    assertTrue(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserNewName() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakareAuthorized);
    when(puService.getPerson(personnummer))
        .thenReturn(
            PersonSvar.found(new Person(personnummer, false, false, "Ny", "", "User", "", "", "")));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Ny User", user.getName());
    assertEquals(PersonSvar.Status.FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.AUTHORIZED, user.getStatus());
    assertTrue(user.isNameFromPuService());
    assertTrue(user.isNameUpdated());
  }

  @Test
  public void testGetUserNotInPUService() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
    when(puService.getPerson(personnummer)).thenReturn(PersonSvar.notFound());

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.NOT_FOUND, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
    assertFalse(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserErrorFromPUService() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
    when(puService.getPerson(personnummer)).thenReturn(PersonSvar.error());

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.ERROR, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
    assertFalse(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test
  public void testGetUserCantContactPUService() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);
    when(puService.getPerson(personnummer)).thenThrow(
        new WebServiceException("Could not send message"));

    User user = userService.getUserWithStatus();
    assertEquals(PERSON_ID, user.getPersonalIdentityNumber());
    assertEquals("Test User", user.getName());
    assertEquals(PersonSvar.Status.ERROR, user.getPersonSvarStatus());
    assertEquals(RegistrationStatus.NOT_STARTED, user.getStatus());
    assertFalse(user.isNameFromPuService());
    assertFalse(user.isNameUpdated());
  }

  @Test(expected = PrivatlakarportalServiceException.class)
  public void testGetUserNoLoggedInUser() {
    SecurityContextHolder.clearContext();
    userService.getUserWithStatus();
  }

  @Test
  public void testGetUserWithStatusInvalidPnr() {
    SecurityContextHolder.setContext(getSecurityContext(PERSON_ID_INVALID, "Invalid pnr User"));
    when(privatlakareRepository.findByPersonId(PERSON_ID_INVALID)).thenReturn(
        privatlakareInvalidPnr);

    User user = userService.getUserWithStatus();
    assertEquals(PersonSvar.Status.ERROR, user.getPersonSvarStatus());
  }

  // Create a fake SecurityContext for a user
  private SecurityContext getSecurityContext(final String personId, final String name) {
    final PrivatlakarUser user = new PrivatlakarUser(personId, name, "authScheme");
    return new SecurityContext() {
      @Override
      public void setAuthentication(Authentication authentication) {
      }

      @Override
      public Authentication getAuthentication() {
        return new Authentication() {
          @Override
          public Object getPrincipal() {
            return user;
          }

          @Override
          public boolean isAuthenticated() {
            return true;
          }

          @Override
          public String getName() {
            return "questionResource";
          }

          @Override
          public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
          }

          @Override
          public Object getCredentials() {
            return null;
          }

          @Override
          public Object getDetails() {
            return null;
          }

          @Override
          public void setAuthenticated(boolean isAuthenticated) {
          }
        };
      }
    };
  }

  private Personnummer createPnr(String pnr) {
    return Personnummer.createPersonnummer(pnr).get();
  }
}
