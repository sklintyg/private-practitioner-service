package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.bootstrap;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_INIT_DATA_PROFILE;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
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
        log.info("Loading private practitioner and adding it to db if it does not already exist {}",
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
      log.info("Private practitioner {} created", privatlakareEntity.getFullstandigtNamn());
    }
  }

  public void initPractitioners(List<String> personIds) {
    try {
      final var resolver = new PathMatchingResourcePatternResolver();
      final var files = resolver.getResources("classpath:bootstrap-privatlakare/*.json");
      for (Resource res : files) {
        final var privatlakareEntity = new CustomObjectMapper().readValue(res.getInputStream(),
            PrivatlakareEntity.class);
        if (personIds.contains(privatlakareEntity.getPersonId())) {
          log.info(
              "Loading private practitioner and adding it to db if it does not already exist {}",
              res.getFilename());
          if (privatlakareEntityRepository.findByPersonId(privatlakareEntity.getPersonId())
              .isEmpty()) {
            privatlakareEntityRepository.save(privatlakareEntity);
            log.info("Private practitioner {} created", privatlakareEntity.getFullstandigtNamn());
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

  }
}
