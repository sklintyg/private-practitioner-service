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

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

  String personId;
  String name;

  String befattning;
  String verksamhetensNamn;
  @Builder.Default
  String agarForm = "Privat";
  String vardform;
  String verksamhetstyp;
  String arbetsplatskod;

  String telefonnummer;
  String epost;
  String adress;
  String postnummer;
  String postort;
  String kommun;
  String lan;

  Long godkannandeMedgivandeVersion;

  public boolean checkIsValid() {
    return checkValues(
        befattning,
        verksamhetensNamn,
        agarForm,
        vardform,
        verksamhetstyp,
        telefonnummer,
        epost,
        adress,
        postnummer,
        postort,
        kommun,
        lan
    );
  }

  private boolean checkValues(String... strings) {
    return Stream.of(strings).noneMatch(StringUtils::isBlank);
  }
}
