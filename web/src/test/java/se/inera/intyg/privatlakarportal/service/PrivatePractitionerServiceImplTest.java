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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import se.inera.intyg.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.intyg.privatlakarportal.persistence.model.Befattning;
import se.inera.intyg.privatlakarportal.persistence.model.Medgivande;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.model.Vardform;
import se.inera.intyg.privatlakarportal.persistence.model.Verksamhetstyp;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.model.PrivatePractitioner;

@RunWith(MockitoJUnitRunner.class)
public class PrivatePractitionerServiceImplTest {

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @InjectMocks
  private PrivatePractitionerServiceImpl privatePractitionerService;

  @Test
  public void getPrivatePractitioners_ok() throws IOException {
    Privatlakare p1 = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    Privatlakare p2 = readPrivatlakare("RegisterServiceImplTest/test.json");

    when(privatlakareRepository.findAll()).thenReturn(List.of(p1, p2));

    List<PrivatePractitioner> privatePractitioners = privatePractitionerService.getPrivatePractitioners();

    assertNotNull(privatePractitioners);
    assertTrue(privatePractitioners.size() == 2);


  }

  @Test
  public void getPrivatePractitioners_empty_ok() {
    List<PrivatePractitioner> privatePractitioners = privatePractitionerService.getPrivatePractitioners();

    assertNotNull(privatePractitioners);
    assertTrue(privatePractitioners.isEmpty());
  }

  @Test
  public void getPrivatePractitioner_hsaId_ok() throws IOException {
    Privatlakare privatlakare = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    String hsaId = privatlakare.getHsaId();

    when(privatlakareRepository.findByHsaId(hsaId)).thenReturn(privatlakare);

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        hsaId);

    verify(privatlakareRepository, times(1)).findByHsaId(anyString());
    verify(privatlakareRepository, times(0)).findByPersonId(anyString());

    assertNotNull(privatePractitioner);
    assertEquals(privatlakare.getHsaId(), privatePractitioner.getHsaId());
    assertEquals(privatlakare.getFullstandigtNamn(), privatePractitioner.getName());
    assertEquals(privatlakare.getVardgivareNamn(), privatePractitioner.getCareproviderName());
    assertEquals(privatlakare.getEpost(), privatePractitioner.getEmail());
    assertEquals(privatlakare.getRegistreringsdatum(), privatePractitioner.getRegistrationDate());

  }

  @Test
  public void getPrivatePractitioner_personId_ok() throws IOException {
    Privatlakare privatlakare = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    String personId = privatlakare.getPersonId();

    when(privatlakareRepository.findByPersonId(personId)).thenReturn(privatlakare);

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        personId);

    verify(privatlakareRepository, times(0)).findByHsaId(anyString());
    verify(privatlakareRepository, times(1)).findByPersonId(anyString());

    assertNotNull(privatePractitioner);
    assertEquals(privatlakare.getHsaId(), privatePractitioner.getHsaId());
    assertEquals(privatlakare.getFullstandigtNamn(), privatePractitioner.getName());
    assertEquals(privatlakare.getVardgivareNamn(), privatePractitioner.getCareproviderName());
    assertEquals(privatlakare.getEpost(), privatePractitioner.getEmail());
    assertEquals(privatlakare.getRegistreringsdatum(), privatePractitioner.getRegistrationDate());

  }

  @Test
  public void getPrivatePractitioner_notFound() {

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        "notFound");

    assertNull(privatePractitioner);

  }

  @Test
  public void getPrivatePractitioner_missingPersonOrHsaId() {

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        null);

    assertNull(privatePractitioner);
  }

  private Privatlakare readPrivatlakare(String path) throws IOException {
    Privatlakare verifyPrivatlakare = new CustomObjectMapper().readValue(new ClassPathResource(
        path).getFile(), Privatlakare.class);
    for (Befattning befattning : verifyPrivatlakare.getBefattningar()) {
      befattning.setPrivatlakare(verifyPrivatlakare);
    }
    for (Verksamhetstyp verksamhetstyp : verifyPrivatlakare.getVerksamhetstyper()) {
      verksamhetstyp.setPrivatlakare(verifyPrivatlakare);
    }
    for (Vardform vardform : verifyPrivatlakare.getVardformer()) {
      vardform.setPrivatlakare(verifyPrivatlakare);
    }
    for (Medgivande medgivande : verifyPrivatlakare.getMedgivande()) {
      medgivande.setPrivatlakare(verifyPrivatlakare);
    }
    return verifyPrivatlakare;
  }
}
