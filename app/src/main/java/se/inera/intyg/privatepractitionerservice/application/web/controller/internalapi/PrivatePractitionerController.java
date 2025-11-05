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
package se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.privatepractitionerservice.application.service.EraseService;
import se.inera.intyg.privatepractitionerservice.application.service.PrivatePractitionerService;
import se.inera.intyg.privatepractitionerservice.application.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.PrivatePractitionerDto;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerRequest;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.IntegrationService;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.PerformanceLogging;

@RestController
@RequestMapping("/internalapi/privatepractitioner")
public class PrivatePractitionerController {

  private final PrivatePractitionerService privatePractitionerService;
  private final IntegrationService integrationService;
  private final EraseService eraseService;

  @Autowired
  public PrivatePractitionerController(PrivatePractitionerService privatePractitionerService,
      IntegrationService integrationService,
      EraseService eraseService) {
    this.privatePractitionerService = privatePractitionerService;
    this.integrationService = integrationService;
    this.eraseService = eraseService;
  }

  @GetMapping("")
  @PerformanceLogging(eventAction = "get-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ResponseEntity<PrivatePractitionerDto> getPrivatePractitioner(
      @RequestParam String personOrHsaId) {
    PrivatePractitioner privatePractitioner = privatePractitionerService.getPrivatePractitioner(
        personOrHsaId);

    if (privatePractitioner == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(convert(privatePractitioner));
  }

  @GetMapping("/all")
  @PerformanceLogging(eventAction = "get-private-practitioners", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public ResponseEntity<List<PrivatePractitionerDto>> getPrivatePractitioners() {
    List<PrivatePractitioner> privatePractitioners = privatePractitionerService.getPrivatePractitioners();

    return ResponseEntity.ok(convert(privatePractitioners));
  }

  @GetMapping("/validate")
  @PerformanceLogging(eventAction = "validate-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_INFO)
  public ResponseEntity<ValidatePrivatePractitionerResponse> validatePrivatePractitioner(
      @RequestBody ValidatePrivatePractitionerRequest request) {
    final var response = integrationService.validatePrivatePractitionerByPersonId(
        request.getPersonalIdentityNumber());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/erase/{id}")
  @PerformanceLogging(eventAction = "erase-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_DELETION)
  public void erasePrivatePractitioner(@PathVariable("id") String careProviderId) {
    eraseService.erasePrivatePractitioner(careProviderId);
  }

  private List<PrivatePractitionerDto> convert(List<PrivatePractitioner> privatePractitioners) {
    if (privatePractitioners == null) {
      return List.of();
    }

    return privatePractitioners.stream()
        .map(this::convert)
        .collect(Collectors.toList());

  }

  private PrivatePractitionerDto convert(PrivatePractitioner privatePractitioner) {
    if (privatePractitioner == null) {
      return null;
    }

    return new PrivatePractitionerDto(privatePractitioner.getHsaId(),
        privatePractitioner.getPersonId(), privatePractitioner.getName(),
        privatePractitioner.getCareproviderName(), privatePractitioner.getEmail(),
        privatePractitioner.getRegistrationDate());
  }
}
