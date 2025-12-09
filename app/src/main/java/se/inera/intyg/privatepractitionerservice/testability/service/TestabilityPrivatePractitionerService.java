package se.inera.intyg.privatepractitionerservice.testability.service;

import static se.inera.intyg.privatepractitionerservice.testability.common.TestabilityConstants.TESTABILITY_PROFILE;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.bootstrap.PrivatlakarBootstrapBean;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;
import se.inera.intyg.privatepractitionerservice.testability.dto.TestabilityCreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.testability.service.converter.TestabilityPrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.testability.service.factory.TestabilityPrivatePractitionerFactory;

@Slf4j
@Profile(TESTABILITY_PROFILE)
@Service
@Transactional
@RequiredArgsConstructor
public class TestabilityPrivatePractitionerService {

  private final TestabilityPrivatePractitionerFactory privatePractitionerFactory;
  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final TestabilityPrivatePractitionerConverter privatePractitionerConverter;
  private final PrivatlakareEntityRepository privatlakareEntityRepository;
  private final Optional<PrivatlakarBootstrapBean> privatlakarBootstrapBean;


  public PrivatePractitionerDTO createRegistration(
      TestabilityCreateRegistrationRequest registration) {

    final var privatePractitioner = privatePractitionerFactory.create(registration);

    final var savedPrivatePractitioner = privatePractitionerRepository.save(privatePractitioner);

    return privatePractitionerConverter.convert(savedPrivatePractitioner);
  }

  public void remove(List<String> personIds) {
    personIds.forEach(p -> {
      try {
        privatlakareEntityRepository.delete(
            privatlakareEntityRepository.findByPersonId(p).orElseThrow());
      } catch (Exception e) {
        log.info("Could not delete private practitioner with personId {}", p);
      }
    });
  }

  public void clear() {
    privatePractitionerRepository.clear();
  }

  public void addExisting(List<String> personIds) {
    remove(personIds);
    privatlakarBootstrapBean.orElseThrow().initPractitioners(personIds);
  }

  public void initDate() {
    clear();
    privatlakarBootstrapBean.orElseThrow().initData();
  }
}


