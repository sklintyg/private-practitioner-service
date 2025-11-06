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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@Service
public class HospPersonServiceImpl implements HospPersonService {

  private static final Logger LOG = LoggerFactory.getLogger(HospPersonServiceImpl.class);
  private static final String OK = "OK";

  @Autowired
  private HospService authorizationManagementService;

  @Override
  public HospPerson getHospPerson(String personId) {

    LOG.debug("Getting hospPerson from HSA for '{}'", personId);

    HospCredentialsForPerson response = authorizationManagementService.getHospCredentialsForPersonResponseType(
        personId);

    if (response == null || response.getPersonalIdentityNumber() == null) {
      LOG.debug("Response did not contain any hospPerson for '{}'", personId);
      return null;
    }
    HospPerson hospPerson = new HospPerson();

    hospPerson.setPersonalIdentityNumber(response.getPersonalIdentityNumber());
    hospPerson.setPersonalPrescriptionCode(response.getPersonalPrescriptionCode());

    List<String> hsaTitles = new ArrayList<>();
    List<String> titleCodes = new ArrayList<>();
    if (response.getHealthCareProfessionalLicence() != null
        && response.getHealthCareProfessionalLicence().size() > 0) {
      for (HealthCareProfessionalLicence licence : response.getHealthCareProfessionalLicence()) {
        hsaTitles.add(licence.getHealthCareProfessionalLicenceName());
        titleCodes.add(licence.getHealthCareProfessionalLicenceCode());
      }
    }
    hospPerson.setHsaTitles(hsaTitles);
    hospPerson.setTitleCodes(titleCodes);

    List<String> specialityNames = new ArrayList<>();
    List<String> specialityCodes = new ArrayList<>();
    if (response.getHealthCareProfessionalLicenceSpeciality() != null
        && response.getHealthCareProfessionalLicenceSpeciality().size() > 0) {
      for (HCPSpecialityCodes codes : response.getHealthCareProfessionalLicenceSpeciality()) {
        specialityNames.add(codes.getSpecialityName());
        specialityCodes.add(codes.getSpecialityCode());
      }
    }
    hospPerson.setSpecialityNames(specialityNames);
    hospPerson.setSpecialityCodes(specialityCodes);

    return hospPerson;
  }

  @Override
  public LocalDateTime getHospLastUpdate() {

    LOG.debug("Calling getHospLastUpdate");

    return authorizationManagementService.getHospLastUpdate();
  }

  @Override
  public boolean addToCertifier(String personId, String certifierId) {
    return handleCertifier(true, personId, certifierId, null);
  }

  @Override
  public boolean removeFromCertifier(String personId, String certifierId, String reason) {
    return handleCertifier(false, personId, certifierId, reason);
  }

  private boolean handleCertifier(boolean add, String personId, String certifierId, String reason) {

    LOG.debug("Calling handleCertifier for certifierId '{}'", certifierId);
    Result result = authorizationManagementService
        .handleHospCertificationPersonResponseType(certifierId, add ? "add" : "remove", personId,
            reason);

    if (!OK.equals(result.getResultCode())) {
      LOG.error("handleCertifier returned result '{}' for certifierId '{}'", result.getResultText(),
          certifierId);
      return false;
    }

    return true;
  }

}
