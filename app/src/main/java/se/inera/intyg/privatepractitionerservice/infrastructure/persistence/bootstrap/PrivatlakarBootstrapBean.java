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
package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.bootstrap;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_INIT_DATA_PROFILE;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.infrastructure.config.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;

@Profile({"dev", TESTABILITY_INIT_DATA_PROFILE})
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivatlakarBootstrapBean {

  private final PrivatlakareEntityRepository privatlakareEntityRepository;

  @PostConstruct
  public void initData() {
    try {
      final var resolver = new PathMatchingResourcePatternResolver();
      final var files = resolver.getResources("classpath:bootstrap-privatlakare/*.json");
      for (Resource res : files) {
        log.info("Loading privatlakare and adding it to db if it does not already exist {}",
            res.getFilename());
        addPrivatlakare(res);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void addPrivatlakare(Resource res) throws IOException {
    final var privatlakareEntity = new CustomObjectMapper().readValue(res.getInputStream(),
        PrivatlakareEntity.class);
    if (privatlakareEntityRepository.findByPersonId(privatlakareEntity.getPersonId()).isEmpty()) {
      privatlakareEntityRepository.save(privatlakareEntity);
    }
  }
}
