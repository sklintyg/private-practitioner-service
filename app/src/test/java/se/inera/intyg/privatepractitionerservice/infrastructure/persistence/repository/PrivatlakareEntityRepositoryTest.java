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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({"h2"})
@Transactional
@Disabled("Disabled due to legacy database initialization issues. Need to be replaced with DataJpaTest")
class PrivatlakareEntityRepositoryTest {

  @Autowired
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @BeforeEach
  void clear() {
    privatlakareEntityRepository.deleteAll();
  }

  @Test
  public void testFindByPersonId() {
    PrivatlakareEntity saved = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("191212121212", 1, true));
    PrivatlakareEntity read = privatlakareEntityRepository.findByPersonId(saved.getPersonId());
    PrivatlakareEntity read2 = privatlakareEntityRepository.findByHsaId(saved.getHsaId());

    assertEquals(read, read2);

    assertThat(read.getAgarform(), is(equalTo(saved.getAgarform())));
    assertThat(read.getArbetsplatsKod(), is(equalTo(saved.getArbetsplatsKod())));
    assertThat(read.getEnhetsId(), is(equalTo(saved.getEnhetsId())));
    assertThat(read.getEnhetsNamn(), is(equalTo(saved.getEnhetsNamn())));
    assertThat(read.getEnhetSlutDatum(), is(equalTo(saved.getEnhetSlutDatum())));
    assertThat(read.getEnhetStartdatum(), is(equalTo(saved.getEnhetStartdatum())));
    assertThat(read.getEpost(), is(equalTo(saved.getEpost())));
    assertThat(read.getForskrivarKod(), is(equalTo(saved.getForskrivarKod())));
    assertThat(read.getFullstandigtNamn(), is(equalTo(saved.getFullstandigtNamn())));
    assertThat(read.isGodkandAnvandare(), is(equalTo(saved.isGodkandAnvandare())));
    assertThat(read.getHsaId(), is(equalTo(saved.getHsaId())));
    assertThat(read.getKommun(), is(equalTo(saved.getKommun())));
    assertThat(read.getLan(), is(equalTo(saved.getLan())));
    assertThat(read.getPersonId(), is(equalTo(saved.getPersonId())));
    assertThat(read.getPostadress(), is(equalTo(saved.getPostadress())));
    assertThat(read.getPostnummer(), is(equalTo(saved.getPostnummer())));
    assertThat(read.getPostort(), is(equalTo(saved.getPostort())));
    assertThat(read.getTelefonnummer(), is(equalTo(saved.getTelefonnummer())));
    assertThat(read.getVardgivareId(), is(equalTo(saved.getVardgivareId())));
    assertThat(read.getVardgivareNamn(), is(equalTo(saved.getVardgivareNamn())));
    assertThat(read.getVardgivareSlutdatum(), is(equalTo(saved.getVardgivareSlutdatum())));
    assertThat(read.getVardgivareStartdatum(), is(equalTo(saved.getVardgivareStartdatum())));

    assertArrayEquals(read.getBefattningar().toArray(), saved.getBefattningar().toArray());
    assertArrayEquals(read.getLegitimeradeYrkesgrupper().toArray(),
        saved.getLegitimeradeYrkesgrupper().toArray());
    assertArrayEquals(read.getSpecialiteter().toArray(), saved.getSpecialiteter().toArray());
    assertArrayEquals(read.getVerksamhetstyper().toArray(), saved.getVerksamhetstyper().toArray());
    assertArrayEquals(read.getVardformer().toArray(), saved.getVardformer().toArray());
  }

  @Test
  void testFindWithoutLakarBehorighet() {
    PrivatlakareEntity p1 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true));
    PrivatlakareEntity p2 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false));
    PrivatlakareEntity p3 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false));
    PrivatlakareEntity p4 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true));
    PrivatlakareEntity p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
    p5.setEnhetStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    p5.setVardgivareStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    privatlakareEntityRepository.save(p5);

    List<PrivatlakareEntity> list = privatlakareEntityRepository.findWithoutLakarBehorighet();

    assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
    assertThat("p2 should be in the list, it does not have läkarbehörighet", list.contains(p2));
    assertThat("p3 should be in the list, it does not have läkarbehörighet", list.contains(p3));
    assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
    assertThat("p5 should be in the list, it does not have läkarbehörighet", list.contains(p5));
    assertEquals(3, list.size());
  }

  @Test
  void testFindNeverHadLakarBehorighet() {
    PrivatlakareEntity p1 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true));
    PrivatlakareEntity p2 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false));
    PrivatlakareEntity p3 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false));
    PrivatlakareEntity p4 = privatlakareEntityRepository.save(
        PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true));
    PrivatlakareEntity p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
    p5.setEnhetStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    p5.setVardgivareStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    privatlakareEntityRepository.save(p5);

    List<PrivatlakareEntity> list = privatlakareEntityRepository.findNeverHadLakarBehorighet();

    assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
    assertThat("p2 should be in the list, it never had läkarbehörighet", list.contains(p2));
    assertThat("p3 should be in the list, it never had läkarbehörighet", list.contains(p3));
    assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
    assertThat(
        "p5 should not be in the list, it previously had läkarbehörighet and logged in to webcert",
        !list.contains(p5));
    assertEquals(2, list.size());
  }

  @Test
  void testFindNeverHadBehorighetAndRegisteredBefore() {
    PrivatlakareEntity p1 = PrivatelakareTestUtil.buildPrivatlakare("p1", 1, true);
    privatlakareEntityRepository.save(p1);
    PrivatlakareEntity p2 = PrivatelakareTestUtil.buildPrivatlakare("p2", 2, false);
    p2.setRegistreringsdatum(LocalDate.parse("2015-09-30").atStartOfDay());
    privatlakareEntityRepository.save(p2);
    PrivatlakareEntity p3 = PrivatelakareTestUtil.buildPrivatlakare("p3", 3, false);
    p3.setRegistreringsdatum(LocalDate.parse("2014-01-01").atStartOfDay());
    privatlakareEntityRepository.save(p3);
    PrivatlakareEntity p4 = PrivatelakareTestUtil.buildPrivatlakare("p4", 4, true);
    privatlakareEntityRepository.save(p4);
    PrivatlakareEntity p5 = PrivatelakareTestUtil.buildPrivatlakare("p5", 5, false);
    p5.setEnhetStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    p5.setVardgivareStartdatum(LocalDate.parse("2015-08-01").atStartOfDay());
    privatlakareEntityRepository.save(p5);
    PrivatlakareEntity p6 = PrivatelakareTestUtil.buildPrivatlakare("p6", 6, false);
    p6.setRegistreringsdatum(LocalDate.parse("2015-10-01").atStartOfDay());
    privatlakareEntityRepository.save(p6);

    LocalDateTime date = LocalDate.parse("2015-09-30").atStartOfDay();
    List<PrivatlakareEntity> list = privatlakareEntityRepository.findNeverHadLakarBehorighetAndRegisteredBefore(
        date);

    assertThat("p1 should not be in the list, it has läkarbehörighet", !list.contains(p1));
    assertThat(
        "p2 should be in the list, it never had läkarbehörighet and registered on 2015-09-30",
        list.contains(p2));
    assertThat(
        "p3 should be in the list, it never had läkarbehörighet and registered before 2015-09-30",
        list.contains(p3));
    assertThat("p4 should not be in the list, it has läkarbehörighet", !list.contains(p4));
    assertThat(
        "p5 should not be in the list, it previously had läkarbehörighet and logged in to webcert",
        !list.contains(p5));
    assertThat(
        "p6 should not be in the list, it never had läkarbehörighet but registered after 2015-09-30",
        !list.contains(p6));
    assertEquals(2, list.size());
  }

}
