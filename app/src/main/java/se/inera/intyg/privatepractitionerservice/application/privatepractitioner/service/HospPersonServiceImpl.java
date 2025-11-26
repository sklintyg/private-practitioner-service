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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HCPSpecialityCodes;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HealthCareProfessionalLicence;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.HospCredentialsForPerson.RestrictionDTO;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.model.Result;

@Service
@RequiredArgsConstructor
public class HospPersonServiceImpl implements HospPersonService {

  private static final Logger LOG = LoggerFactory.getLogger(HospPersonServiceImpl.class);
  private static final String OK = "OK";

  private final HospService hospService;

  @Override
  public HospPerson getHospPerson(String personId) {

    LOG.debug("Getting hospPerson from HSA for '{}'", personId);

    HospCredentialsForPerson response = hospService.getHospCredentialsForPersonResponseType(
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
    if (!response.getHealthCareProfessionalLicence().isEmpty()) {
      for (HealthCareProfessionalLicence licence : response.getHealthCareProfessionalLicence()) {
        hsaTitles.add(licence.getHealthCareProfessionalLicenceName());
        titleCodes.add(licence.getHealthCareProfessionalLicenceCode());
      }
    }
    hospPerson.setHsaTitles(hsaTitles);
    hospPerson.setTitleCodes(titleCodes);

    List<String> specialityNames = new ArrayList<>();
    List<String> specialityCodes = new ArrayList<>();
    if (!response.getHealthCareProfessionalLicenceSpeciality().isEmpty()) {
      for (HCPSpecialityCodes codes : response.getHealthCareProfessionalLicenceSpeciality()) {
        specialityNames.add(codes.getSpecialityName());
        specialityCodes.add(codes.getSpecialityCode());
      }
    }
    hospPerson.setSpecialityNames(specialityNames);
    hospPerson.setSpecialityCodes(specialityCodes);
    setRestrictions(response, hospPerson);

    return hospPerson;
  }

  private static void setRestrictions(HospCredentialsForPerson response, HospPerson hospPerson) {
    List<String> restrictionNames = new ArrayList<>();
    List<String> restrictionCodes = new ArrayList<>();
    if (!response.getRestrictions().isEmpty()) {
      for (RestrictionDTO restriction : response.getRestrictions()) {
        restrictionNames.add(restriction.getRestrictionName());
        restrictionCodes.add(restriction.getRestrictionCode());
      }
    }
    hospPerson.setRestrictionNames(restrictionNames);
    hospPerson.setRestrictionCodes(restrictionCodes);
  }

  @Override
  public LocalDateTime getHospLastUpdate() {

    LOG.debug("Calling getHospLastUpdate");

    return hospService.getHospLastUpdate();
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
    Result result = hospService.handleHospCertificationPersonResponseType(certifierId,
        add ? "add" : "remove", personId,
        reason);

    if (!OK.equals(result.getResultCode())) {
      LOG.error("handleCertifier returned result '{}' for certifierId '{}'", result.getResultText(),
          certifierId);
      return false;
    }

    return true;
  }

}
