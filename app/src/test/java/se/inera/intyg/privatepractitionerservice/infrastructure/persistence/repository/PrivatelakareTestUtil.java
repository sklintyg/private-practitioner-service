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
package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.BefattningEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VardformEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VerksamhetstypEntity;

public final class PrivatelakareTestUtil {

  private PrivatelakareTestUtil() {
  }

  // CHECKSTYLE:OFF ParameterNumber
  public static PrivatlakareEntity buildPrivatlakare(String personId, int hsaCounter,
      boolean isLakare) {
    return buildPrivatlakare(
        personId,
        "SE000000000000-WEBCERT0000" + hsaCounter,
        "Tolvan Tolvansson",
        "test@example.com",
        "4444444444",
        "postadress",
        "postnummer",
        "postort",
        "2015-08-01",
        isLakare);
  }
  // CHECKSTYLE:ON ParameterNumber

  // CHECKSTYLE:OFF ParameterNumber
  public static PrivatlakareEntity buildPrivatlakare(
      String personId, String hsaId, String fullstandigtNamn, String epost, String telefonnummer,
      String postadress, String postnummer, String postort, String registreringsdatum,
      boolean isLakare) {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();

    privatlakareEntity.setPersonId(personId);
    privatlakareEntity.setHsaId(hsaId);
    privatlakareEntity.setFullstandigtNamn(fullstandigtNamn);
    privatlakareEntity.setEpost(epost);
    privatlakareEntity.setTelefonnummer(telefonnummer);
    privatlakareEntity.setPostadress(postadress);
    privatlakareEntity.setPostnummer(postnummer);
    privatlakareEntity.setPostort(postort);
    privatlakareEntity.setForskrivarKod("0000000");

    privatlakareEntity.setAgarform("Privat");
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setArbetsplatsKod("0000000000");

    privatlakareEntity.setLan("Län");
    privatlakareEntity.setKommun("Kommun");

    privatlakareEntity.setEnhetsId(hsaId);
    privatlakareEntity.setEnhetsNamn("Enhetsnamn");
    if (isLakare) {
      privatlakareEntity.setEnhetStartdatum(LocalDate.parse("2015-06-01").atStartOfDay());
      privatlakareEntity.setEnhetSlutDatum(LocalDate.parse("2015-06-02").atStartOfDay());
    }

    privatlakareEntity.setVardgivareId(hsaId);
    privatlakareEntity.setVardgivareNamn("Vardgivarnamn");
    if (isLakare) {
      privatlakareEntity.setVardgivareStartdatum(LocalDate.parse("2015-06-03").atStartOfDay());
      privatlakareEntity.setVardgivareSlutdatum(LocalDate.parse("2015-06-04").atStartOfDay());
    }

    Set<BefattningEntity> befattningar = new HashSet<>();
    befattningar.add(new BefattningEntity(privatlakareEntity, "Befattning kod 1"));
    befattningar.add(new BefattningEntity(privatlakareEntity, "Befattning kod 2"));
    privatlakareEntity.setBefattningar(befattningar);

    Set<LegitimeradYrkesgruppEntity> legitimeradeYrkesgrupper = new HashSet<>();
    if (isLakare) {
      legitimeradeYrkesgrupper.add(
          new LegitimeradYrkesgruppEntity(privatlakareEntity, "Läkare", "LK"));
    }
    privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);

    List<SpecialitetEntity> specialiteter = new ArrayList<>();
    specialiteter.add(
        new SpecialitetEntity(privatlakareEntity, "Specialitet kod 1", "Specialitet namn 1"));
    specialiteter.add(
        new SpecialitetEntity(privatlakareEntity, "Specialitet kod 2", "Specialitet namn 2"));
    privatlakareEntity.setSpecialiteter(specialiteter);

    Set<VerksamhetstypEntity> verksamhetsTyper = new HashSet<>();
    verksamhetsTyper.add(new VerksamhetstypEntity(privatlakareEntity, "Verksamhetstyp 1"));
    verksamhetsTyper.add(new VerksamhetstypEntity(privatlakareEntity, "Verksamhetstyp 2"));
    privatlakareEntity.setVerksamhetstyper(verksamhetsTyper);

    Set<VardformEntity> vardformer = new HashSet<>();
    vardformer.add(new VardformEntity(privatlakareEntity, "Vardform 1"));
    vardformer.add(new VardformEntity(privatlakareEntity, "Vardform 2"));
    privatlakareEntity.setVardformer(vardformer);

    privatlakareEntity.setRegistreringsdatum(LocalDate.parse(registreringsdatum).atStartOfDay());

    return privatlakareEntity;
  }
  // CHECKSTYLE:ON ParameterNumber

}
