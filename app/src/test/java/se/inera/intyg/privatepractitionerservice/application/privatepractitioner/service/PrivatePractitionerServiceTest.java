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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerServiceTest {

  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private PrivatePractitionerConverter privatePractitionerConverter;
  @Mock
  private UpdatePrivatePractitionerFromPUService updatePrivatePractitionerFromPUService;
  @InjectMocks
  private PrivatePractitionerService privatePractitionerService;

  @Test
  void shouldReturnNullIfPersonOrHsaIdIsNull() {
    final var actual = privatePractitionerService.getPrivatePractitioner(null);

    assertNull(actual);
  }

  @Test
  void shouldReturnNullIfPersonOrHsaIdDoesntExist() {
    final var personId = "personId";

    final var actual = privatePractitionerService.getPrivatePractitioner(personId);

    assertNull(actual);
  }

  @Test
  void shouldReturnPrivatePractitionerFromPersonId() {
    final var expected = PrivatePractitionerDTO.builder().hsaId("HSA123").build();

    final var personId = "personId";
    final var privatePractitioner = PrivatePractitioner.builder().hsaId("HSA123").build();
    when(privatePractitionerRepository.findByPersonId(personId)).thenReturn(
        Optional.of(privatePractitioner)
    );
    when(updatePrivatePractitionerFromPUService.updateFromPu(privatePractitioner)).thenReturn(
        privatePractitioner);
    when(privatePractitionerConverter.convert(privatePractitioner)).thenReturn(expected);

    final var actual = privatePractitionerService.getPrivatePractitioner(personId);

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnPrivatePractitionerFromHsaId() {
    final var expected = PrivatePractitionerDTO.builder().hsaId("HSA123").build();

    final var hsaId = "hsaId";
    final var privatePractitioner = PrivatePractitioner.builder().hsaId("HSA123").build();
    when(privatePractitionerRepository.findByPersonId(hsaId)).thenReturn(
        Optional.empty()
    );
    when(privatePractitionerRepository.findByHsaId(hsaId)).thenReturn(
        Optional.of(privatePractitioner)
    );
    when(privatePractitionerConverter.convert(privatePractitioner)).thenReturn(expected);

    final var actual = privatePractitionerService.getPrivatePractitioner(hsaId);

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnPrivatePractitionersWhenExists() {
    final var expected = List.of(
        PrivatePractitionerDTO.builder().hsaId("HSA123").build(),
        PrivatePractitionerDTO.builder().hsaId("HSA456").build()
    );

    final var privatePractitionerOne = PrivatePractitioner.builder().hsaId("HSA123").build();
    final var privatePractitionerTwo = PrivatePractitioner.builder().hsaId("HSA456").build();

    when(privatePractitionerRepository.findAll()).thenReturn(
        List.of(privatePractitionerOne, privatePractitionerTwo)
    );
    when(privatePractitionerConverter.convert(privatePractitionerOne)).thenReturn(
        expected.getFirst()
    );
    when(privatePractitionerConverter.convert(privatePractitionerTwo)).thenReturn(
        expected.getLast()
    );

    final var actual = privatePractitionerService.getPrivatePractitioners();

    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnPrivatePractitionersWhenEmpty() {
    final var expected = List.of();

    when(privatePractitionerRepository.findAll()).thenReturn(List.of());

    final var actual = privatePractitionerService.getPrivatePractitioners();

    assertEquals(expected, actual);
  }
}
