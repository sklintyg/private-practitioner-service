package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EraseService {

  @Value("${erase.private.practitioner:true}")
  private boolean erasePrivatePractitioner;

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final HospRepository hospRepository;
  private final MonitoringLogService monitoringLogService;

  @Transactional
  public void erasePrivatePractitioner(String careProviderId) {
    final var privatePractitioner = privatePractitionerRepository.findByHsaId(careProviderId);

    if (privatePractitioner.isEmpty()) {
      log.warn("Could not find private practitioner with hsa-id {}. Nothing was erased.",
          careProviderId);
      return;
    }

    if (!erasePrivatePractitioner) {
      log.warn(
          "Erase private practitioner is inactivated via configuration. Private practitioner with hsa-id {} was not erased.",
          careProviderId);
      return;
    }

    if (hospRepository.removeFromCertifier(privatePractitioner.get(),
        "Avslutat konto i Webcert.")) {
      privatePractitionerRepository.remove(privatePractitioner.get());
      monitoringLogService.logUserErased(privatePractitioner.get().getPersonId(), careProviderId);
      log.info("Erased private practitioner with hsa-id {}.", careProviderId);
    } else {
      log.error("Failure unregistering private practitioner {} in certifier branch.",
          privatePractitioner.get().getHsaId());
      throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.EXTERNAL_ERROR,
          "Failure unregistering private practitioner in certifier branch.");
    }
  }
}
