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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

/**
 * Created by pebe on 2015-06-24.
 */
@Repository
public interface PrivatlakareEntityRepository extends JpaRepository<PrivatlakareEntity, String> {

  @Query("SELECT p from PrivatlakareEntity p WHERE p.hsaId = :hsaId")
  Optional<PrivatlakareEntity> findByHsaId(@Param("hsaId") String hsaId);

  @Query("SELECT p from PrivatlakareEntity p WHERE p.personId = :personId")
  Optional<PrivatlakareEntity> findByPersonId(@Param("personId") String personId);

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare')")
  List<PrivatlakareEntity> findWithoutLakarBehorighet();

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL")
  List<PrivatlakareEntity> findNeverHadLakarBehorighet();

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL "
      + "AND p.registreringsdatum <= :beforeDate")
  List<PrivatlakareEntity> findNeverHadLakarBehorighetAndRegisteredBefore(
      @Param("beforeDate") LocalDateTime beforeDate);

}
