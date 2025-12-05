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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetHospInformationResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.RegistrationConfigurationResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.CreateRegistrationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.EraseService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.GetHospInformationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.PrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.RegistrationConfigurationService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.UpdatePrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.ValidatePrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.PerformanceLogging;

@RestController
@RequestMapping("/internalapi/privatepractitioner")
@RequiredArgsConstructor
public class PrivatePractitionerController {

  private final CreateRegistrationService createRegistrationService;
  private final UpdatePrivatePractitionerService updatePrivatePractitionerService;
  private final PrivatePractitionerService privatePractitionerService;
  private final RegistrationConfigurationService registrationConfigurationService;
  private final GetHospInformationService getHospInformationService;
  private final ValidatePrivatePractitionerService validatePrivatePractitionerService;
  private final EraseService eraseService;

  @PostMapping("")
  @PerformanceLogging(eventAction = "register-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_CREATION)
  public ResponseEntity<PrivatePractitionerDTO> registerPrivatePractitioner(
      @RequestBody CreateRegistrationRequest createRegistrationRequest) {
    final var privatePractitionerDTO = createRegistrationService.createRegistration(
        createRegistrationRequest
    );
    return ResponseEntity.ok(privatePractitionerDTO);
  }

  @GetMapping("/configuration")
  @PerformanceLogging(eventAction = "registration-configuration", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ResponseEntity<RegistrationConfigurationResponse> getRegistrationConfiguration() {
    final var registrationConfigurationResponse = registrationConfigurationService.get();
    return ResponseEntity.ok(registrationConfigurationResponse);
  }

  @PostMapping("/hosp")
  @PerformanceLogging(eventAction = "get-hosp-information", eventType = MdcLogConstants.EVENT_TYPE_INFO)
  public ResponseEntity<GetHospInformationResponse> getHospInformation(
      @RequestBody GetHospInformationRequest getHospInformationRequest) {
    final var getHospInformationResponse = getHospInformationService.get(getHospInformationRequest);
    return ResponseEntity.ok(getHospInformationResponse);
  }

  @GetMapping("")
  @PerformanceLogging(eventAction = "get-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ResponseEntity<PrivatePractitionerDTO> getPrivatePractitioner(
      @RequestParam String personOrHsaId) {
    final var privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        personOrHsaId);

    if (privatePractitioner == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(privatePractitioner);
  }

  @GetMapping("/all")
  @PerformanceLogging(eventAction = "get-private-practitioners", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ResponseEntity<List<PrivatePractitionerDTO>> getPrivatePractitioners() {
    final var privatePractitioners = privatePractitionerService.getPrivatePractitioners();
    return ResponseEntity.ok(privatePractitioners);
  }

  @PostMapping("/validate")
  @PerformanceLogging(eventAction = "validate-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_INFO)
  public ResponseEntity<ValidatePrivatePractitionerResponse> validatePrivatePractitioner(
      @RequestBody ValidatePrivatePractitionerRequest request) {
    final var response = validatePrivatePractitionerService.validate(request.getPersonId());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PerformanceLogging(eventAction = "erase-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_DELETION)
  public void erasePrivatePractitioner(@PathVariable("id") String careProviderId) {
    eraseService.erasePrivatePractitioner(careProviderId);
  }

  @PutMapping("")
  @PerformanceLogging(eventAction = "update-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
  public ResponseEntity<PrivatePractitionerDTO> updatePrivatePractitioner(
      @RequestBody UpdatePrivatePractitionerRequest request) {
    final var updatedPrivatePractitioner = updatePrivatePractitionerService.update(request);
    return ResponseEntity.ok(updatedPrivatePractitioner);
  }
}
