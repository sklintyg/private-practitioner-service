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
package se.inera.intyg.privatepractitionerservice.testability;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_PROFILE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.testability.service.TestabilityPrivatePractitionerService;

@Slf4j
@Profile(TESTABILITY_PROFILE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/testability/")
public class TestabilityPrivatePractitionerController {

  private final TestabilityPrivatePractitionerService testabilityPrivatePractitionerService;

  @PostMapping("/privatepractitioner")
  public ResponseEntity<PrivatePractitionerDTO> registerPrivatePractitioner(
      @RequestBody TestabilityCreateRegistrationRequest createRegistrationRequest) {
    log.info(
        "Testability API - Registering private practitioner: {}",
        createRegistrationRequest.getPersonId());
    final var privatePractitionerDTO =
        testabilityPrivatePractitionerService.createRegistration(createRegistrationRequest);
    return ResponseEntity.ok(privatePractitionerDTO);
  }

  @PostMapping("/add-existing")
  public void add(@RequestBody List<String> personIds) {
    testabilityPrivatePractitionerService.addExisting(personIds);
  }

  @DeleteMapping("/reset")
  public void reset() {
    clear();
    testabilityPrivatePractitionerService.initDate();
  }

  @PutMapping("/hsa-id-counter")
  public void updateId(@RequestBody Integer id) {
    testabilityPrivatePractitionerService.setPrivateLakarId(id);
  }

  @DeleteMapping("/clear")
  public void clear() {
    testabilityPrivatePractitionerService.clear();
  }

  @DeleteMapping("/privatepractitioner")
  public void remove(@RequestBody List<String> personIds) {
    testabilityPrivatePractitionerService.remove(personIds);
  }
}
