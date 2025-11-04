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
package se.inera.intyg.privatlakarportal.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.integration.postnummer.service.PostnummerService;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.logging.MdcLogConstants;
import se.inera.intyg.privatlakarportal.logging.PerformanceLogging;
import se.inera.intyg.privatlakarportal.service.RegisterService;
import se.inera.intyg.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.intyg.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.CreateRegistrationRequest;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.CreateRegistrationResponse;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.GetHospInformationResponse;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.GetOmradeResponse;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.GetRegistrationResponse;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.SaveRegistrationRequest;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.SaveRegistrationResponse;

/**
 * Created by pebe on 2015-06-25.
 */
@RestController
@RequestMapping("/api/registration")
public class RegisterController {

  @Autowired
  private RegisterService registerService;

  @Autowired
  private PostnummerService postnummerService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  @PerformanceLogging(eventAction = "get-registration", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public GetRegistrationResponse getRegistration() {
    RegistrationWithHospInformation registrationWithHospInformation = registerService.getRegistration();
    return new GetRegistrationResponse(registrationWithHospInformation.getRegistration(),
        registrationWithHospInformation.getHospInformation(),
        registrationWithHospInformation.isWebcertUserTermsApproved());
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
  @PerformanceLogging(eventAction = "create-registration", eventType = MdcLogConstants.EVENT_TYPE_CREATION)
  public CreateRegistrationResponse createRegistration(
      @RequestBody CreateRegistrationRequest request) {
    RegistrationStatus status = registerService.createRegistration(request.getRegistration(),
        request.getGodkantMedgivandeVersion());
    return new CreateRegistrationResponse(status);
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
  @PerformanceLogging(eventAction = "save-registration", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
  public SaveRegistrationResponse createRegistration(@RequestBody SaveRegistrationRequest request) {
    SaveRegistrationResponseStatus status = registerService.saveRegistration(
        request.getRegistration());
    return new SaveRegistrationResponse(status);
  }

  @RequestMapping(value = "/hospInformation", method = RequestMethod.GET)
  @PerformanceLogging(eventAction = "get-hosp-information-information", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public GetHospInformationResponse getHospInformation() {
    return new GetHospInformationResponse(registerService.getHospInformation());
  }

  @RequestMapping(value = "/omrade/{postnummer}", method = RequestMethod.GET)
  @PerformanceLogging(eventAction = "get-omrade", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public GetOmradeResponse getOmrade(@PathVariable("postnummer") String postnummer) {
    return new GetOmradeResponse(postnummerService.getOmradeByPostnummer(postnummer));
  }
}
