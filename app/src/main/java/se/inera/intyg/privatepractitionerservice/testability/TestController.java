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
package se.inera.intyg.privatepractitionerservice.testability;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.RegisterService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.HospUpdateService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.IntegrationService;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.PerformanceLogging;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospUppdateringEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;
import se.inera.intyg.privatepractitionerservice.testability.dto.PrivatlakareDto;

/**
 * Created by pebe on 2015-09-02.
 */
@RestController
@RequestMapping("/api/test")
@Profile({"dev", "testability-api"})
public class TestController {

  private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
  private static final int YEARS_BACK_IN_TIME = 10;

  @Autowired
  private RegisterService registerService;

  @Autowired
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @Autowired
  private HospUppdateringEntityRepository hospUppdateringEntityRepository;

  @Autowired
  private HospUpdateService hospUpdateService;

  @Autowired
  private IntegrationService integrationService;

  public TestController() {
    LOG.error("testability-api enabled. DO NOT USE IN PRODUCTION");
  }

  @RequestMapping(value = "/registration/{id}", method = RequestMethod.GET)
  @ResponseBody
  @PerformanceLogging(eventAction = "test-api-get-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public PrivatlakareDto getPrivatlakare(@PathVariable("id") String personId) {
    return registerService.getPrivatlakare(personId);
  }

  @RequestMapping(value = "/registration/remove/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  @PerformanceLogging(eventAction = "test-api-remove-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_DELETION)
  public boolean removePrivatlakare(@PathVariable("id") String personId) {
    return registerService.removePrivatlakare(personId);
  }

  @RequestMapping(value = "/registration/setname/{id}", method = RequestMethod.POST)
  @Transactional(transactionManager = "transactionManager")
  @PerformanceLogging(eventAction = "test-api-set-name-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
  public boolean setNamePrivatlakare(@PathVariable("id") String personId,
      @RequestBody String name) {
    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(personId);
    if (privatlakareEntity == null) {
      LOG.error("Unable to find privatlakare with personId '{}'", personId);
      return false;
    }
    privatlakareEntity.setFullstandigtNamn(name);
    privatlakareEntityRepository.save(privatlakareEntity);
    return true;
  }

  @RequestMapping(value = "/registration/setregistrationdate/{id}", method = RequestMethod.POST)
  @Transactional(transactionManager = "transactionManager")
  @PerformanceLogging(eventAction = "test-api-set-registration-date-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
  public boolean setRegistrationDatePrivatlakare(@PathVariable("id") String personId,
      @RequestBody String date) {
    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(personId);
    if (privatlakareEntity == null) {
      LOG.error("Unable to find privatlakare with personId '{}'", personId);
      return false;
    }
    privatlakareEntity.setRegistreringsdatum(LocalDate.parse(date).atStartOfDay());
    privatlakareEntityRepository.save(privatlakareEntity);
    return true;
  }

  @RequestMapping(value = "/webcert/validatePrivatePractitioner/{id}", method = RequestMethod.POST)
  @PerformanceLogging(eventAction = "test-api-validate-private-practitioner", eventType = MdcLogConstants.EVENT_TYPE_INFO)
  public ValidatePrivatePractitionerResponse validatePrivatePractitioner(
      @PathVariable("id") String id) {
    return integrationService.validatePrivatePractitionerByPersonId(id);
  }
}
