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

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;

/**
 * Created by pebe on 2015-06-24.
 */
public interface PrivatlakareRepository extends JpaRepository<Privatlakare, String> {

  @Query("SELECT p from Privatlakare p WHERE p.hsaId = :hsaId")
  Privatlakare findByHsaId(@Param("hsaId") String hsaId);

  @Query("SELECT p from Privatlakare p WHERE p.personId = :personId")
  Privatlakare findByPersonId(@Param("personId") String personId);

  @Query("SELECT p FROM Privatlakare p WHERE "
      + "p NOT IN (SELECT ly.privatlakare FROM LegitimeradYrkesgrupp ly WHERE ly.namn = 'Läkare')")
  List<Privatlakare> findWithoutLakarBehorighet();

  @Query("SELECT p FROM Privatlakare p WHERE "
      + "p NOT IN (SELECT ly.privatlakare FROM LegitimeradYrkesgrupp ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL")
  List<Privatlakare> findNeverHadLakarBehorighet();

  @Query("SELECT p FROM Privatlakare p WHERE "
      + "p NOT IN (SELECT ly.privatlakare FROM LegitimeradYrkesgrupp ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL "
      + "AND p.registreringsdatum <= :beforeDate")
  List<Privatlakare> findNeverHadLakarBehorighetAndRegisteredBefore(
      @Param("beforeDate") LocalDateTime beforeDate);

}
