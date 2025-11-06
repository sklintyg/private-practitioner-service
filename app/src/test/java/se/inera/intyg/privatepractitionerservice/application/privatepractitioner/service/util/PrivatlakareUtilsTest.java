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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

/**
 * Created by pebe on 2015-09-07.
 */
class PrivatlakareUtilsTest {

  @Test
  void testLakare() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    Set<LegitimeradYrkesgruppEntity> legitimeradYrkesgrupperEntity = new HashSet<LegitimeradYrkesgruppEntity>();
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity(privatlakareEntity, "Extra", "E"));
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity(privatlakareEntity, "Läkare", "LK"));
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity(privatlakareEntity, "Mer", "M"));
    privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupperEntity);

    assertTrue(PrivatlakareUtils.hasLakareLegitimation(privatlakareEntity));
  }

  @Test
  void testEjLakare() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    Set<LegitimeradYrkesgruppEntity> legitimeradYrkesgrupperEntity = new HashSet<LegitimeradYrkesgruppEntity>();
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity(privatlakareEntity, "Extra", "E"));
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity(privatlakareEntity, "Mer", "M"));
    privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupperEntity);

    assertFalse(PrivatlakareUtils.hasLakareLegitimation(privatlakareEntity));
  }

}
