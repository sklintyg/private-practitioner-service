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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_HSA_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataConstants.DR_KRANSTEGE_PERSON_ID;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_DTO;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataDTO.DR_KRANSTEGE_UPDATE_REQUEST;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator.UpdatePrivatePractitionerRequestValidator;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class UpdatePrivatePractitionerServiceTest {

  @Mock private PrivatePractitionerRepository repository;
  @Mock private UpdatePrivatePractitionerRequestValidator validator;
  @Mock private PrivatePractitionerConverter converter;
  @Mock private HashUtility hashUtility;
  @Mock private MonitoringLogService monitoringLogService;
  @InjectMocks private UpdatePrivatePractitionerService service;

  @Test
  void shouldThrowExceptionWhenValidationFails() {
    doThrow(PrivatlakarportalServiceException.class)
        .when(validator)
        .validate(DR_KRANSTEGE_UPDATE_REQUEST);
    assertThrows(
        PrivatlakarportalServiceException.class, () -> service.update(DR_KRANSTEGE_UPDATE_REQUEST));
  }

  @Test
  void shouldThrowExceptionWhenPrivatePractitionerNotFound() {
    when(repository.findByPersonId(DR_KRANSTEGE_UPDATE_REQUEST.getPersonId()))
        .thenReturn(Optional.empty());

    assertThrows(
        PrivatlakarportalServiceException.class, () -> service.update(DR_KRANSTEGE_UPDATE_REQUEST));
  }

  @Test
  void shouldReturnUpdatedPrivatePractitioner() {
    when(repository.findByPersonId(DR_KRANSTEGE_UPDATE_REQUEST.getPersonId()))
        .thenReturn(Optional.of(kranstegeBuilder().build()));

    when(repository.save(any(PrivatePractitioner.class))).thenReturn(kranstegeBuilder().build());

    when(converter.convert(any(PrivatePractitioner.class))).thenReturn(DR_KRANSTEGE_DTO);

    final var result = service.update(DR_KRANSTEGE_UPDATE_REQUEST);

    assertNotNull(result);
    verify(repository).save(any(PrivatePractitioner.class));
    verify(converter).convert(any(PrivatePractitioner.class));
    assertEquals(DR_KRANSTEGE_DTO, result);
  }

  @Test
  void shouldNotifyPrivatePractitionerUpdate() {

    when(repository.findByPersonId(DR_KRANSTEGE_UPDATE_REQUEST.getPersonId()))
        .thenReturn(Optional.of(kranstegeBuilder().build()));

    when(repository.save(any(PrivatePractitioner.class))).thenReturn(kranstegeBuilder().build());

    service.update(DR_KRANSTEGE_UPDATE_REQUEST);

    verify(monitoringLogService).logUserDetailsChanged(DR_KRANSTEGE_PERSON_ID, DR_KRANSTEGE_HSA_ID);
  }
}
