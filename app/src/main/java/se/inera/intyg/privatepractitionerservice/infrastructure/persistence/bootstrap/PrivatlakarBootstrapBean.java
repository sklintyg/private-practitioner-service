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

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.infrastructure.config.CustomObjectMapper;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;

@Service
@Profile({"dev"})
public class PrivatlakarBootstrapBean {

  private static final Logger LOG = LoggerFactory.getLogger(PrivatlakarBootstrapBean.class);

  @Autowired
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @PostConstruct
  public void initData() {

    List<Resource> files = getResourceListing("classpath:bootstrap-privatlakare/*.json");
    for (Resource res : files) {
      LOG.info("Loading privatlakare resource " + res.getFilename());
      addPrivatlakare(res);
    }
  }

  private void addPrivatlakare(Resource res) {

    try {
      PrivatlakareEntity privatlakareEntity = new CustomObjectMapper().readValue(
          res.getInputStream(),
          PrivatlakareEntity.class);
      if (privatlakareEntityRepository.findByPersonId(privatlakareEntity.getPersonId()) == null) {
        privatlakareEntityRepository.save(privatlakareEntity);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private List<Resource> getResourceListing(String classpathResourcePath) {
    try {
      PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
      return Arrays.asList(r.getResources(classpathResourcePath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
