/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.NO_ACCOUNT;
import static se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode.OK;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Restriction;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class ValidatePrivatePractitionerServiceTest {

  @Mock private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock private HashUtility hashUtility;
  @Mock private HospRepository hospRepository;
  @InjectMocks private ValidatePrivatePractitionerService validatePrivatePractitionerService;

  @Test
  void shouldReturnNoAccountIfNoPrivatePractitionerExists() {
    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.empty());

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(NO_ACCOUNT, actual.getResultCode());
  }

  @Test
  void shouldRetrieveUpdatedHospWhenPrivatePractitionerExists() {
    final var drKranstege = kranstegeBuilder().build();
    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstege));

    validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);

    verify(hospRepository).updatedHospPerson(drKranstege);
  }

  @Test
  void shouldUpdatedHospWhenUpdatedHospInformationExists() {
    final var drKranstegeMock = mock(PrivatePractitioner.class);

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeMock));
    when(hospRepository.updatedHospPerson(drKranstegeMock))
        .thenReturn(Optional.of(DR_KRANSTEGE_HOSP_PERSON));

    validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);

    verify(drKranstegeMock).updateWithHospInformation(DR_KRANSTEGE_HOSP_PERSON);
  }

  @Test
  void shouldSaveStartDateIfFirstLogin() {
    final var drKranstegeFirstLogin = kranstegeBuilder().startDate(null).build();

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeFirstLogin));

    validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);

    final var captor = ArgumentCaptor.forClass(PrivatePractitioner.class);

    verify(privatePractitionerRepository).save(captor.capture());

    assertTrue(
        Math.abs(ChronoUnit.SECONDS.between(LocalDateTime.now(), captor.getValue().getStartDate()))
            <= 1,
        "startDate should be within 1 second");
  }

  @Test
  void shouldReturnOKIfPrivatePractitionerIsLicensedPhysician() {
    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(kranstegeBuilder().build()));

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(OK, actual.getResultCode());
  }

  @Test
  void shouldReturnNotAuthorizedInHoppIfPrivatePractitionerIsNotLicensedPhysician() {
    final var drKranstegeNotLicensed =
        kranstegeBuilder().licensedHealthcareProfessions(List.of()).build();

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeNotLicensed));

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(NOT_AUTHORIZED_IN_HOSP, actual.getResultCode());
  }

  @Test
  void shouldReturnOKIfPrivatePractitionerIsNotRestrictedPhysician() {
    final var drKranstegeRestricted = kranstegeBuilder().restrictions(List.of()).build();

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeRestricted));

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(OK, actual.getResultCode());
  }

  @Test
  void shouldReturnNotAuthorizedInHospIfPrivatePractitionerIsRestrictedPhysician() {
    final var drKranstegeRestricted =
        kranstegeBuilder()
            .restrictions(
                List.of(
                    new Restriction[] {new Restriction("001", "Återkallad legitimation", "LK")}))
            .build();

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeRestricted));

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(NOT_AUTHORIZED_IN_HOSP, actual.getResultCode());
  }

  @Test
  void shouldReturnOKIfPrivatePractitionerHasRestrictionsButIsNotRestrictedPhysician() {
    final var drKranstegeRestricted =
        kranstegeBuilder()
            .restrictions(
                List.of(new Restriction[] {new Restriction("002", "Treårig prövotid", "LK")}))
            .build();

    when(privatePractitionerRepository.findByPersonId(DR_KRANSTEGE_PERSON_ID))
        .thenReturn(Optional.of(drKranstegeRestricted));

    final var actual = validatePrivatePractitionerService.validate(DR_KRANSTEGE_PERSON_ID);
    assertEquals(OK, actual.getResultCode());
  }
}
