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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.IntegrationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.PrivatePractitionerService;

@ExtendWith(MockitoExtension.class)
class PrivatePractitionerControllerTest {

  private static final String PERSONAL_IDENTITY_NUMBER = "191212121212";

  @Mock
  private PrivatePractitionerService privatePractitionerService;

  @Mock
  private IntegrationService integrationService;

  @InjectMocks
  private PrivatePractitionerController privatePractitionerController;

  @Test
  void shouldReturnPrivatePractitioners() {
    final var expected = List.of(
        PrivatePractitionerDTO.builder().hsaId("123").build(),
        PrivatePractitionerDTO.builder().hsaId("456").build()
    );

    when(privatePractitionerService.getPrivatePractitioners()).thenReturn(expected);

    final var actual = privatePractitionerController.getPrivatePractitioners();

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(expected, actual.getBody())
    );
  }

  @Test
  void shouldReturnPrivatePractitioner() {
    final var expected = PrivatePractitionerDTO.builder().hsaId("123").build();

    when(privatePractitionerService.getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER))
        .thenReturn(expected);

    final var actual = privatePractitionerController.getPrivatePractitioner(
        PERSONAL_IDENTITY_NUMBER);

    assertAll(
        () -> assertEquals(HttpStatus.OK, actual.getStatusCode()),
        () -> assertEquals(expected, actual.getBody())
    );
  }

  @Test
  void shouldReturnPrivatePractitionerNotFound() {
    when(privatePractitionerService.getPrivatePractitioner(PERSONAL_IDENTITY_NUMBER))
        .thenReturn(null);

    final var actual = privatePractitionerController.getPrivatePractitioner(
        PERSONAL_IDENTITY_NUMBER);

    assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
  }
}
