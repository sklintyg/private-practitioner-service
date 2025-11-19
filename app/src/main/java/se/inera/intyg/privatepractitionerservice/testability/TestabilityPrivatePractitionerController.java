package se.inera.intyg.privatepractitionerservice.testability;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_PROFILE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    log.info("Testability API - Registering private practitioner: {}",
        createRegistrationRequest.getPersonId());
    final var privatePractitionerDTO = testabilityPrivatePractitionerService.createRegistration(
        createRegistrationRequest
    );
    return ResponseEntity.ok(privatePractitionerDTO);
  }

  @DeleteMapping("/privatepractitioner")
  public void reset(
      @RequestBody List<String> testabilityPrivatePractitionerRequest) {
    testabilityPrivatePractitionerService.reset(testabilityPrivatePractitionerRequest);
  }
}
