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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.util.List;

public class HospInformation {

  String personalPrescriptionCode;
  List<String> specialityNames;
  List<String> hsaTitles;

  public String getPersonalPrescriptionCode() {
    return personalPrescriptionCode;
  }

  public void setPersonalPrescriptionCode(String personalPrescriptionCode) {
    this.personalPrescriptionCode = personalPrescriptionCode;
  }

  public List<String> getSpecialityNames() {
    return specialityNames;
  }

  public void setSpecialityNames(List<String> specialityNames) {
    this.specialityNames = specialityNames;
  }

  public List<String> getHsaTitles() {
    return hsaTitles;
  }

  public void setHsaTitles(List<String> hsaTitles) {
    this.hsaTitles = hsaTitles;
  }
}
