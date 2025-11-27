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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static java.time.temporal.ChronoUnit.MINUTES;
import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.SPAN_ID_KEY;
import static se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants.TRACE_ID_KEY;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.util.PrivatlakareUtils;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcCloseableMap;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcHelper;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.PerformanceLogging;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.HospUppdateringEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.RestriktionEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospUppdateringEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;

/**
 * Created by pebe on 2015-09-03.
 */
@Service
public class HospUpdateServiceImpl implements HospUpdateService {

  private static final Logger LOG = LoggerFactory.getLogger(HospUpdateServiceImpl.class);
  private static final String JOB_NAME = "hospupdate.cron";

  private static final long MINUTES_PER_DAY = 1440;

  @Autowired
  PrivatlakareEntityRepository privatlakareEntityRepository;

  @Autowired
  HospUppdateringEntityRepository hospUppdateringEntityRepository;

  @Autowired
  HospPersonService hospPersonService;

  @Autowired
  MailService mailService;

  @Value("${privatlakarportal.hospupdate.interval}")
  private long mailInterval;

  @Value("${privatlakarportal.hospupdate.emails}")
  private int numberOfEmails;

  @Autowired
  private MonitoringLogService monitoringService;

  @Autowired
  private MdcHelper mdcHelper;

  private LocalDateTime lastUpdate;

  @PostConstruct
  private void setupTimeBetweenUpdates() {
    lastUpdate = LocalDateTime.now();
  }

  @Override
  @Scheduled(cron = "${privatlakarportal.hospupdate.cron}")
  @SchedulerLock(name = JOB_NAME)
  @Transactional
  @PerformanceLogging(eventAction = "scheduled-update-hosp-information", eventType = MdcLogConstants.EVENT_TYPE_INFO)
  public void scheduledUpdateHospInformation() {
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(TRACE_ID_KEY, mdcHelper.traceId())
            .put(SPAN_ID_KEY, mdcHelper.spanId())
            .build()
    ) {
      String skipUpdate = System.getProperty("scheduled.update.skip", "false");
      LOG.debug("scheduled.update.skip = " + skipUpdate);
      if ("true".equalsIgnoreCase(skipUpdate)) {
        LOG.info("Skipping scheduled updateHospInformation");
      } else {
        LOG.info("Starting scheduled updateHospInformation");
        updateHospInformation();
      }
    }
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public void updateHospInformation() {
    // Get our last hosp update time from database
    HospUppdateringEntity hospUppdateringEntity = hospUppdateringEntityRepository.findSingle();

    LocalDateTime now = LocalDateTime.now();

    // Get last hosp update time from HSA
    LocalDateTime hsaHospLastUpdate;
    try {
      hsaHospLastUpdate = hospPersonService.getHospLastUpdate();
    } catch (Exception e) {
      LOG.error("Failed to getHospLastUpdate from HSA with exception {}", e.getMessage(), e);
      return;
    }

    // If hospUppdatering is null this is our first update ever
    if (hospUppdateringEntity == null
        || hospUppdateringEntity.getSenasteHospUppdatering().isBefore(hsaHospLastUpdate)) {

      LOG.info("Hospinformation has been updated in HSA since our last update");

      // Save hosp update time in database
      if (hospUppdateringEntity == null) {
        hospUppdateringEntity = new HospUppdateringEntity(hsaHospLastUpdate);
      } else {
        hospUppdateringEntity.setSenasteHospUppdatering(hsaHospLastUpdate);
      }
      hospUppdateringEntityRepository.save(hospUppdateringEntity);

      // Find privatlakare without hospinformation
      List<PrivatlakareEntity> privatlakareEntityList = privatlakareEntityRepository.findNeverHadLakarBehorighet();
      for (PrivatlakareEntity privatlakareEntity : privatlakareEntityList) {
        try {
          RegistrationStatus status = updateHospInformation(privatlakareEntity, true);
          // Check if information has been updated
          if (status.equals(RegistrationStatus.AUTHORIZED)
              || status.equals(RegistrationStatus.NOT_AUTHORIZED)) {
            privatlakareEntityRepository.save(privatlakareEntity);
            mailService.sendRegistrationStatusEmail(status, privatlakareEntity.getEpost());
          } else if (status.equals(RegistrationStatus.WAITING_FOR_HOSP)) {
            privatlakareEntityRepository.save(privatlakareEntity);
            handleWaitingForHosp(now, privatlakareEntity, status);
          }
        } catch (HospUpdateFailedToContactHsaException e) {
          LOG.error("Failed to contact HSA with error '{}'", e.getMessage(), e);
        }
      }
      lastUpdate = now;
    }
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public RegistrationStatus updateHospInformation(PrivatlakareEntity privatlakareEntity,
      boolean shouldRegisterInCertifier)
      throws HospUpdateFailedToContactHsaException {

    if (shouldRegisterInCertifier) {
      try {
        if (!hospPersonService.addToCertifier(privatlakareEntity.getPersonId(),
            privatlakareEntity.getHsaId())) {
          LOG.error(
              "Failed to call handleCertifier in HSA, this call will be retried at next hosp update cycle.");
        }
      } catch (Exception e) {
        LOG.error(
            "Failed to call handleCertifier in HSA with error {}, this call will be retried at next hosp update cycle.",
            e.getMessage(), e);
        throw new HospUpdateFailedToContactHsaException(e);
      }
    }

    HospPerson hospPersonResponse;
    try {
      hospPersonResponse = hospPersonService.getHospPerson(privatlakareEntity.getPersonId());
    } catch (Exception e) {
      LOG.error(
          "Failed to call getHospPerson in HSA, this call will be retried at next hosp update cycle.");
      throw new HospUpdateFailedToContactHsaException(e);
    }

    if (hospPersonResponse == null) {
      if (privatlakareEntity.getLegitimeradeYrkesgrupper() != null) {
        privatlakareEntity.getLegitimeradeYrkesgrupper().clear();
      }
      if (privatlakareEntity.getSpecialiteter() != null) {
        privatlakareEntity.getSpecialiteter().clear();
      }

      privatlakareEntity.getRestriktioner().clear();
      privatlakareEntity.setForskrivarKod(null);

      monitoringService.logHospWaiting(privatlakareEntity.getPersonId(),
          privatlakareEntity.getHsaId());
      return RegistrationStatus.WAITING_FOR_HOSP;
    } else {

      updateRestriktioner(privatlakareEntity, hospPersonResponse);
      updateSpecialiteter(privatlakareEntity, hospPersonResponse);
      updateLegitimeradeYrkesgrupper(privatlakareEntity, hospPersonResponse);

      privatlakareEntity.setForskrivarKod(hospPersonResponse.getPersonalPrescriptionCode());

      if (PrivatlakareUtils.hasLakareLegitimation(privatlakareEntity)) {
        monitoringService.logUserAuthorizedInHosp(privatlakareEntity.getPersonId(),
            privatlakareEntity.getHsaId());
        if (!privatlakareEntity.isGodkandAnvandare()) {
          return RegistrationStatus.NOT_AUTHORIZED;
        }
        return RegistrationStatus.AUTHORIZED;
      } else {
        monitoringService.logUserNotAuthorizedInHosp(privatlakareEntity.getPersonId(),
            privatlakareEntity.getHsaId());
        return RegistrationStatus.NOT_AUTHORIZED;
      }
    }
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public void checkForUpdatedHospInformation(PrivatlakareEntity privatlakareEntity) {
    try {
      LocalDateTime hospLastUpdate = hospPersonService.getHospLastUpdate();
      if (privatlakareEntity.getSenasteHospUppdatering() == null
          || privatlakareEntity.getSenasteHospUppdatering().isBefore(hospLastUpdate)) {

        LOG.debug(
            "Hosp has been updated since last login for privlakare '{}'. Updating hosp information",
            privatlakareEntity.getPersonId());

        try {
          updateHospInformation(privatlakareEntity, false);
          privatlakareEntity.setSenasteHospUppdatering(hospLastUpdate);
          privatlakareEntityRepository.save(privatlakareEntity);
        } catch (HospUpdateFailedToContactHsaException e) {
          LOG.error("Failed to update hosp information for privatlakare '{}' due to {}",
              privatlakareEntity.getPersonId(), e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      LOG.error(
          "Failed to getHospLastUpdate from HSA in checkForUpdatedHospInformation for privatlakare '{}' due to {}",
          privatlakareEntity.getPersonId(), e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public void resetTimer() {
    lastUpdate = LocalDate.MIN.atStartOfDay();
  }

  private void handleWaitingForHosp(LocalDateTime now, PrivatlakareEntity privatlakareEntity,
      RegistrationStatus status) {
    // We should only remove a privatlakare if the grace period has passed and we can remove it from HSA as well.
    if (isTimeToRemoveRegistration(privatlakareEntity.getRegistreringsdatum(), now)) {
      if (hospPersonService.removeFromCertifier(
          privatlakareEntity.getPersonId(), privatlakareEntity.getHsaId(),
          "Inte kunnat verifiera läkarbehörighet på minst "
              + (mailInterval * numberOfEmails) / MINUTES_PER_DAY + " dagar")) {
        // Remove registration as this is the third attempt without success
        LOG.info("Removing {} from registration repo", privatlakareEntity.getPersonId());
        privatlakareEntityRepository.delete(privatlakareEntity);
        mailService.sendRegistrationRemovedEmail(privatlakareEntity);
        monitoringService.logRegistrationRemoved(privatlakareEntity.getPersonId(),
            privatlakareEntity.getHsaId());
      } else {
        // Try again later and only remove privatlakare if they are removed in HSA as well
        LOG.warn("Could not contact HSA to remove privatlakare from certifier");
        return;
      }
    } else {
      for (int i = 1; i < numberOfEmails; i++) {
        if (isTimeToNotifyAboutAwaitingHospStatus(privatlakareEntity.getRegistreringsdatum(), i,
            now)) {
          LOG.info("Sending AWAITING_HOSP mail to {}", privatlakareEntity.getPersonId());
          mailService.sendRegistrationStatusEmail(status, privatlakareEntity.getEpost());
          return; // Only ever send one email
        }
      }
    }
  }

  private boolean isTimeToNotifyAboutAwaitingHospStatus(LocalDateTime registreringsdatum, int n,
      LocalDateTime now) {
    LocalDateTime date = registreringsdatum.plusMinutes(n * mailInterval);
    return date.isAfter(lastUpdate) && date.isBefore(now);
  }

  private boolean isTimeToRemoveRegistration(LocalDateTime registrationDate, LocalDateTime now) {
    return MINUTES.between(registrationDate, now) >= (mailInterval * numberOfEmails);
  }

  private List<SpecialitetEntity> getSpecialiteter(HospPerson hospPersonResponse) {
    return hospPersonResponse.getSpecialities().stream()
        .map(speciality -> new SpecialitetEntity(speciality.name(), speciality.code()))
        .toList();
  }

  private List<RestriktionEntity> getRestriktioner(HospPerson hospPersonResponse) {
    return hospPersonResponse.getRestrictions().stream()
        .map(restriction -> new RestriktionEntity(restriction.name(), restriction.code()))
        .toList();
  }

  private List<LegitimeradYrkesgruppEntity> getLegitimeradeYrkesgrupper(
      HospPerson hospPersonResponse) {
    return hospPersonResponse.getLicensedHealthcareProfessions().stream()
        .map(profession -> new LegitimeradYrkesgruppEntity(profession.name(), profession.code()))
        .toList();
  }

  private void updateRestriktioner(PrivatlakareEntity privatlakareEntity,
      HospPerson hospPersonResponse) {
    if (privatlakareEntity.getRestriktioner() != null) {
      privatlakareEntity.getRestriktioner().clear();
    }
    List<RestriktionEntity> restriktioner = getRestriktioner(
        hospPersonResponse);
    if (privatlakareEntity.getRestriktioner() != null) {
      privatlakareEntity.getRestriktioner().clear();
      privatlakareEntity.getRestriktioner().addAll(restriktioner);
    } else {
      privatlakareEntity.setRestriktioner(restriktioner);
    }
  }

  private void updateLegitimeradeYrkesgrupper(PrivatlakareEntity privatlakareEntity,
      HospPerson hospPersonResponse) {
    final var legitimeradeYrkesgrupper = getLegitimeradeYrkesgrupper(
        hospPersonResponse);
    if (privatlakareEntity.getLegitimeradeYrkesgrupper() != null) {
      privatlakareEntity.getLegitimeradeYrkesgrupper().clear();
      privatlakareEntity.getLegitimeradeYrkesgrupper().addAll(legitimeradeYrkesgrupper);
    } else {
      privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
    }
  }

  private void updateSpecialiteter(PrivatlakareEntity privatlakareEntity,
      HospPerson hospPersonResponse) {
    final var specialiteter = getSpecialiteter(
        hospPersonResponse);

    if (privatlakareEntity.getSpecialiteter() != null) {
      privatlakareEntity.getSpecialiteter().clear();
      privatlakareEntity.getSpecialiteter().addAll(specialiteter);
    } else {
      privatlakareEntity.setSpecialiteter(specialiteter);
    }
  }

}
