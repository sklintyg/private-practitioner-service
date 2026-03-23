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
package se.inera.intyg.privatepractitionerservice.infrastructure.codesystem;

import java.util.List;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HealthcareServiceType;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Position;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.TypeOfCare;

@Repository
public class CodeSystemRepository {

  private final List<HealthcareServiceType> healthcareServiceTypeCodes =
      List.of(
          new HealthcareServiceType("10", "Barn- och ungdomsverksamhet"),
          new HealthcareServiceType("11", "Medicinsk verksamhet"),
          new HealthcareServiceType("12", "Laboratorieverksamhet"),
          new HealthcareServiceType("13", "Kirurgisk verksamhet"),
          new HealthcareServiceType("14", "Övrig medicinsk verksamhet"),
          new HealthcareServiceType("15", "Primärvårdsverksamhet"),
          new HealthcareServiceType("16", "Psykiatrisk verksamhet"),
          new HealthcareServiceType("17", "Radiologisk verksamhet"),
          new HealthcareServiceType("18", "Tandvårdsverksamhet"),
          new HealthcareServiceType("20", "Övrig medicinsk serviceverksamhet"),
          new HealthcareServiceType("21", "Vård-, Omsorg- och Omvårdnadsverksamhet"));

  private final List<Position> positionCodes =
      List.of(
          new Position("201010", "Överläkare"),
          new Position("201011", "Distriktsläkare/Specialist allmänmedicin"),
          new Position("201012", "Skolläkare"),
          new Position("201013", "Företagsläkare"),
          new Position("202010", "Specialistläkare"),
          new Position("203010", "Läkare legitimerad, specialiseringstjänstgöring"),
          new Position("203020", "Läkare legitimerad, bastjänstgöring"),
          new Position("203090", "Läkare legitimerad, annan"));

  private final List<TypeOfCare> typeOfCareCodes =
      List.of(
          new TypeOfCare("01", "Öppenvård"),
          new TypeOfCare("02", "Slutenvård"),
          new TypeOfCare("03", "Hemsjukvård"));

  /** Retrieves the list of HealthcareServiceType codes (Verksamhetstyper). */
  public List<HealthcareServiceType> getHealthcareServiceTypeCodes() {
    return healthcareServiceTypeCodes;
  }

  public boolean healthcareServiceTypeExists(String code) {
    return healthcareServiceTypeCodes.stream()
        .anyMatch(healthcareServiceType -> healthcareServiceType.code().equalsIgnoreCase(code));
  }

  /** Retrieves the list of Position codes (Befattningskoder). */
  public List<Position> getPositionCodes() {
    return positionCodes;
  }

  public boolean positionExists(String code) {
    return positionCodes.stream().anyMatch(position -> position.code().equalsIgnoreCase(code));
  }

  /** Retrieves the list of TypeOfCare codes (Vårdformer). */
  public List<TypeOfCare> getTypeOfCareCodes() {
    return typeOfCareCodes;
  }

  public boolean typeOfCareExists(String code) {
    return typeOfCareCodes.stream()
        .anyMatch(typeOfCare -> typeOfCare.code().equalsIgnoreCase(code));
  }
}
