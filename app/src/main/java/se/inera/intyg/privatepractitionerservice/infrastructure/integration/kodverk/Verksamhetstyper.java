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
package se.inera.intyg.privatepractitionerservice.infrastructure.integration.kodverk;

import java.util.Map;

/**
 * Created by pebe on 2015-08-19.
 */
public final class Verksamhetstyper {

  public static final String VERKSAMHETSTYP_NAME = "Verksamhetskod";
  public static final String VERKSAMHETSTYP_OID = "1.2.752.129.2.2.1.3";
  public static final String VERKSAMHETSTYP_VERSION = "4.1";

  private static final Map<String, String> VERKSAMHETSTYP_MAP = Map.ofEntries(
      Map.entry("10", "Barn- och ungdomsverksamhet"),
      Map.entry("11", "Medicinsk verksamhet"),
      Map.entry("12", "Laboratorieverksamhet"),
      Map.entry("13", "Opererande verksamhet"),
      Map.entry("14", "Övrig medicinsk verksamhet"),
      Map.entry("15", "Primärvårdsverksamhet"),
      Map.entry("16", "Psykiatrisk verksamhet"),
      Map.entry("17", "Radiologisk verksamhet"),
      Map.entry("18", "Tandvårdsverksamhet"),
      Map.entry("20", "Övrig medicinsk serviceverksamhet"),
      Map.entry("21", "Vård-, Omsorg- och Omvårdnadsverksamhet")
  );

  private Verksamhetstyper() {
  }

  public static String getDisplayName(String code) {
    return VERKSAMHETSTYP_MAP.get(code);
  }

  public static Map<String, String> getVerksamhetstyper() {
    return Map.copyOf(VERKSAMHETSTYP_MAP);
  }
}
