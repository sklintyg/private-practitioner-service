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
package se.inera.intyg.privatepractitionerservice.application.web.integration.test.dto;

import java.time.LocalDateTime;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.MedgivandeText;

public class MedgivandeTextDto {

  private String medgivandeText;
  private LocalDateTime datum;

  public MedgivandeTextDto() {
  }

  public MedgivandeTextDto(MedgivandeText mt) {
    this.medgivandeText = mt.getMedgivandeText();
    this.datum = mt.getDatum();
  }

  public String getMedgivandeText() {
    return medgivandeText;
  }

  public void setMedgivandeText(String medgivandeText) {
    this.medgivandeText = medgivandeText;
  }

  public LocalDateTime getDatum() {
    return datum;
  }

  public void setDatum(LocalDateTime datum) {
    this.datum = datum;
  }
}
