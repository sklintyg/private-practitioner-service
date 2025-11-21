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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;

/**
 * Created by pebe on 2015-06-24.
 */
@Transactional(transactionManager = "transactionManager")
@Repository
public interface PrivatlakareIdEntityRepository extends
    JpaRepository<PrivatlakareIdEntity, Integer> {

  @Query("SELECT max(pi.id) FROM PrivatlakareIdEntity pi")
  Integer findLatestGeneratedHsaId();

  @Modifying
  @Query(value = "ALTER TABLE PRIVATLAKARE_ID AUTO_INCREMENT = 1", nativeQuery = true)
  void resetIdSequence();
}
