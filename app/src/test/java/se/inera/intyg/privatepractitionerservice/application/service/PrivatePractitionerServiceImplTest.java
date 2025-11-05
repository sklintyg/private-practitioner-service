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
package se.inera.intyg.privatepractitionerservice.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import se.inera.intyg.privatepractitionerservice.application.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.json.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Befattning;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Medgivande;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Privatlakare;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Vardform;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Verksamhetstyp;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareRepository;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerServiceImplTest {

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @InjectMocks
  private PrivatePractitionerServiceImpl privatePractitionerService;

  @Test
  void getPrivatePractitioners_ok() throws IOException {
    Privatlakare p1 = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    Privatlakare p2 = readPrivatlakare("RegisterServiceImplTest/test.json");

    when(privatlakareRepository.findAll()).thenReturn(List.of(p1, p2));

    List<PrivatePractitioner> privatePractitioners = privatePractitionerService.getPrivatePractitioners();

    assertNotNull(privatePractitioners);
    assertEquals(2, privatePractitioners.size());
  }

  @Test
  void getPrivatePractitioners_empty_ok() {
    List<PrivatePractitioner> privatePractitioners = privatePractitionerService.getPrivatePractitioners();

    assertNotNull(privatePractitioners);
    assertTrue(privatePractitioners.isEmpty());
  }

  @Test
  void getPrivatePractitioner_hsaId_ok() throws IOException {
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
  void getPrivatePractitioner_personId_ok() throws IOException {
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
  void getPrivatePractitioner_notFound() {

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        "notFound");

    assertNull(privatePractitioner);

  }

  @Test
  void getPrivatePractitioner_missingPersonOrHsaId() {

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
