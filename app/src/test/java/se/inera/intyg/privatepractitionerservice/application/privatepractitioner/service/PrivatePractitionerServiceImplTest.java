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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

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
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.config.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.BefattningEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.MedgivandeEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VardformEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VerksamhetstypEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerServiceImplTest {

  @Mock
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @InjectMocks
  private PrivatePractitionerServiceImpl privatePractitionerService;

  @Test
  void getPrivatePractitioners_ok() throws IOException {
    PrivatlakareEntity p1 = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    PrivatlakareEntity p2 = readPrivatlakare("RegisterServiceImplTest/test.json");

    when(privatlakareEntityRepository.findAll()).thenReturn(List.of(p1, p2));

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
    PrivatlakareEntity privatlakareEntity = readPrivatlakare(
        "RegisterServiceImplTest/test_lakare.json");
    String hsaId = privatlakareEntity.getHsaId();

    when(privatlakareEntityRepository.findByHsaId(hsaId)).thenReturn(privatlakareEntity);

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        hsaId);

    verify(privatlakareEntityRepository, times(1)).findByHsaId(anyString());
    verify(privatlakareEntityRepository, times(0)).findByPersonId(anyString());

    assertNotNull(privatePractitioner);
    assertEquals(privatlakareEntity.getHsaId(), privatePractitioner.getHsaId());
    assertEquals(privatlakareEntity.getFullstandigtNamn(), privatePractitioner.getName());
    assertEquals(privatlakareEntity.getVardgivareNamn(), privatePractitioner.getCareproviderName());
    assertEquals(privatlakareEntity.getEpost(), privatePractitioner.getEmail());
    assertEquals(privatlakareEntity.getRegistreringsdatum(),
        privatePractitioner.getRegistrationDate());

  }

  @Test
  void getPrivatePractitioner_personId_ok() throws IOException {
    PrivatlakareEntity privatlakareEntity = readPrivatlakare(
        "RegisterServiceImplTest/test_lakare.json");
    String personId = privatlakareEntity.getPersonId();

    when(privatlakareEntityRepository.findByPersonId(personId)).thenReturn(privatlakareEntity);

    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        personId);

    verify(privatlakareEntityRepository, times(0)).findByHsaId(anyString());
    verify(privatlakareEntityRepository, times(1)).findByPersonId(anyString());

    assertNotNull(privatePractitioner);
    assertEquals(privatlakareEntity.getHsaId(), privatePractitioner.getHsaId());
    assertEquals(privatlakareEntity.getFullstandigtNamn(), privatePractitioner.getName());
    assertEquals(privatlakareEntity.getVardgivareNamn(), privatePractitioner.getCareproviderName());
    assertEquals(privatlakareEntity.getEpost(), privatePractitioner.getEmail());
    assertEquals(privatlakareEntity.getRegistreringsdatum(),
        privatePractitioner.getRegistrationDate());

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

  private PrivatlakareEntity readPrivatlakare(String path) throws IOException {
    PrivatlakareEntity verifyPrivatlakareEntity = new CustomObjectMapper().readValue(
        new ClassPathResource(
            path).getFile(), PrivatlakareEntity.class);
    for (BefattningEntity befattningEntity : verifyPrivatlakareEntity.getBefattningar()) {
      befattningEntity.setPrivatlakare(verifyPrivatlakareEntity);
    }
    for (VerksamhetstypEntity verksamhetstypEntity : verifyPrivatlakareEntity.getVerksamhetstyper()) {
      verksamhetstypEntity.setPrivatlakare(verifyPrivatlakareEntity);
    }
    for (VardformEntity vardformEntity : verifyPrivatlakareEntity.getVardformer()) {
      vardformEntity.setPrivatlakare(verifyPrivatlakareEntity);
    }
    for (MedgivandeEntity medgivandeEntity : verifyPrivatlakareEntity.getMedgivande()) {
      medgivandeEntity.setPrivatlakare(verifyPrivatlakareEntity);
    }
    return verifyPrivatlakareEntity;
  }
}
