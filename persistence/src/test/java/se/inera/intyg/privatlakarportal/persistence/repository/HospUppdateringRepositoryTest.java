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
package se.inera.intyg.privatlakarportal.persistence.repository;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import se.inera.intyg.privatlakarportal.persistence.config.PersistenceConfigDev;
import se.inera.intyg.privatlakarportal.persistence.model.HospUppdatering;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
    PersistenceConfigDev.class})
@ActiveProfiles({"h2"})
public class HospUppdateringRepositoryTest {

  @Autowired
  private HospUppdateringRepository hospUppdateringRepository;

  @Test
  public void testFind() {
    HospUppdatering hospUppdatering = new HospUppdatering();
    hospUppdatering.setSenasteHospUppdatering(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));

    HospUppdatering saved = hospUppdateringRepository.save(hospUppdatering);
    HospUppdatering read = hospUppdateringRepository.findSingle();

    assertEquals(saved.getSenasteHospUppdatering(), read.getSenasteHospUppdatering());
  }
}
