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

import com.google.common.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospInformation;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Registration;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.MedgivandeEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.MedgivandeTextEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.MedgivandeTextEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareIdEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.testability.dto.PrivatlakareDto;

/**
 * Created by pebe on 2015-06-26.
 */
@Service
public class RegisterServiceImpl implements RegisterService {

  @Value("${mail.admin.notification.interval}")
  private int hsaIdNotificationInterval;

  private static final Logger LOG = LoggerFactory.getLogger(RegisterServiceImpl.class);

  @Autowired
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @Autowired
  private MedgivandeTextEntityRepository medgivandeTextEntityRepository;

  @Autowired
  private PrivatlakareIdEntityRepository privatlakareidEntityRepository;

  @Autowired
  private HospPersonService hospPersonService;

  @Autowired
  private HospUpdateService hospUpdateService;

  @Autowired
  private MailService mailService;

  @Autowired
  private DateHelperService dateHelperService;

  @Autowired
  private MonitoringLogService monitoringService;

  @Override
  public HospInformation getHospInformation(String personalIdentityNumber) {
    final var response = hospPersonService.getHospPerson(personalIdentityNumber);

    if (response == null) {
      return null;
    }

    HospInformation hospInformation = new HospInformation();
    hospInformation.setPersonalPrescriptionCode(response.getPersonalPrescriptionCode());
    hospInformation.setSpecialityNames(response.getSpecialityNames());
    hospInformation.setHsaTitles(response.getHsaTitles());

    return hospInformation;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public RegistrationStatus createRegistration(String personalIdentityNumber,
      Registration registration, Long godkantMedgivandeVersion) {

    if (registration == null || !registration.checkIsValid()) {
      LOG.error("createRegistration: CreateRegistrationRequest is not valid");
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "CreateRegistrationRequest is not valid");
    }

    if (godkantMedgivandeVersion == null || godkantMedgivandeVersion <= 0) {
      LOG.error("createRegistration: Not allowed to create registration without medgivande");
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Not allowed to create registration without medgivande");
    }

    if (privatlakareEntityRepository.findByPersonId(personalIdentityNumber) != null) {
      LOG.error("createRegistration: Registration already exists");
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS,
          "Registration already exists");
    }

//    if (!userService.getUser().isNameFromPuService()) {
//      LOG.error(
//          "createRegistration: Not allowed to create registration without updated name from PU-service");
//      throw new PrivatlakarportalServiceException(
//          PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
//          "Not allowed to create registration without updated name from PU-service");
//    }

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();

    privatlakareEntity.setMedgivande(createMedgivandeSet(godkantMedgivandeVersion,
        privatlakareEntity));

    privatlakareEntity.setRegistreringsdatum(dateHelperService.now());
    privatlakareEntity.setPersonId(personalIdentityNumber);
//    privatlakare.setFullstandigtNamn(userService.getUser().getName());
    privatlakareEntity.setGodkandAnvandare(true);

    // PrivatlakareId uses an autoincrement column to get next value
    PrivatlakareIdEntity privatlakareIdEntity = privatlakareidEntityRepository.save(
        new PrivatlakareIdEntity());

    String hsaId = generateHsaId(privatlakareIdEntity);

    privatlakareEntity.setEnhetsId(hsaId);
    privatlakareEntity.setHsaId(hsaId);
    privatlakareEntity.setVardgivareId(hsaId);

    // Set properties from client
    convertRegistrationToPrivatlakare(registration, privatlakareEntity);

    // Lookup hospPerson in HSA
    RegistrationStatus status = null;
    try {
      status = hospUpdateService.updateHospInformation(privatlakareEntity, true);
    } catch (HospUpdateFailedToContactHsaException e) {
      LOG.error("Failed to contact HSA with error {}, setting status {} in the meantime.", e,
          RegistrationStatus.WAITING_FOR_HOSP);
      status = RegistrationStatus.WAITING_FOR_HOSP;
    }

    // Determine if an administrator needs to be notified about HSA ID's running out
    if (privatlakareidEntityRepository.findLatestGeneratedHsaId() != 0
        && privatlakareidEntityRepository.findLatestGeneratedHsaId() % hsaIdNotificationInterval
        == 0) {
      mailService.sendHsaGenerationStatusEmail();
    }

    // Mail notification of WAITING_FOR_HOSP is handled by HospUpdateService
    if (status != RegistrationStatus.WAITING_FOR_HOSP) {
      mailService.sendRegistrationStatusEmail(status, privatlakareEntity);
    }

    privatlakareEntityRepository.save(privatlakareEntity);

    monitoringService.logUserRegistered(privatlakareEntity.getPersonId(), godkantMedgivandeVersion,
        privatlakareEntity.getHsaId(), status);
    return status;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public SaveRegistrationResponseStatus saveRegistration(String personalIdentityNumber,
      Registration registration) {
    if (registration == null || !registration.checkIsValid()) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "SaveRegistrationRequest is not valid");
    }

    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(
        personalIdentityNumber);

    if (privatlakareEntity == null) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.NOT_FOUND,
          "Registration not found");
    }

    convertRegistrationToPrivatlakare(registration, privatlakareEntity);

    privatlakareEntityRepository.save(privatlakareEntity);

    monitoringService.logUserDetailsChanged(privatlakareEntity.getPersonId(),
        privatlakareEntity.getHsaId());

    return SaveRegistrationResponseStatus.OK;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public boolean removePrivatlakare(String personId) {
    PrivatlakareEntity toDelete = privatlakareEntityRepository.findByPersonId(personId);
    if (toDelete == null) {
      LOG.error("No Privatlakare with id {} found in the database!", personId);
      return false;
    }
    privatlakareEntityRepository.delete(toDelete);
    LOG.info("Deleted Privatlakare with id {}", personId);

    monitoringService.logUserDeleted(personId, toDelete.getHsaId());

    return true;
  }

  @VisibleForTesting
  @Override
  public void injectHsaInterval(int hsaIdNotificationInterval) {
    this.hsaIdNotificationInterval = hsaIdNotificationInterval;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public PrivatlakareDto getPrivatlakare(String personId) {
    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(personId);
    if (privatlakareEntity != null) {
      return new PrivatlakareDto(privatlakareEntity);
    }
    return null;
  }

  /* Private helpers */

  /**
   * Generate next hsaId, Format: "SE" + ineras orgnr (inkl "sekelsiffror", alltså 165565594230) +
   * "-" + "WEBCERT" + femsiffrigt löpnr.
   */
  private String generateHsaId(PrivatlakareIdEntity privatlakareIdEntity) {
    // CHECKSTYLE:OFF MagicNumber
    return "SE165565594230-WEBCERT" + StringUtils.leftPad(
        Integer.toString(privatlakareIdEntity.getId()),
        5, '0');
    // CHECKSTYLE:ON MagicNumber
  }

  private Set<MedgivandeEntity> createMedgivandeSet(Long godkantMedgivandeVersion,
      PrivatlakareEntity privatlakareEntity) {
    MedgivandeTextEntity medgivandeTextEntity = medgivandeTextEntityRepository.findById(
            godkantMedgivandeVersion)
        .orElse(null);
    if (medgivandeTextEntity == null) {
      LOG.error("createRegistration: Could not find medgivandetext with version '{}'",
          godkantMedgivandeVersion);
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Could not find medgivandetext matching godkantMedgivandeVersion");
    }
    MedgivandeEntity medgivandeEntity = new MedgivandeEntity();
    medgivandeEntity.setGodkandDatum(dateHelperService.now());
    medgivandeEntity.setMedgivandeText(medgivandeTextEntity);
    medgivandeEntity.setPrivatlakare(privatlakareEntity);
    Set<MedgivandeEntity> medgivandeEntitySet = new HashSet<>();
    medgivandeEntitySet.add(medgivandeEntity);
    return medgivandeEntitySet;
  }

  private void convertRegistrationToPrivatlakare(Registration registration,
      PrivatlakareEntity privatlakareEntity) {
    privatlakareEntity.setAgarform("Privat");
    privatlakareEntity.setArbetsplatsKod(registration.getArbetsplatskod());
    privatlakareEntity.setEnhetsNamn(registration.getVerksamhetensNamn());
    privatlakareEntity.setEpost(registration.getEpost());
    privatlakareEntity.setKommun(registration.getKommun());
    privatlakareEntity.setLan(registration.getLan());
    privatlakareEntity.setPostadress(registration.getAdress());
    privatlakareEntity.setPostnummer(registration.getPostnummer());
    privatlakareEntity.setPostort(registration.getPostort());
    privatlakareEntity.setTelefonnummer(registration.getTelefonnummer());
    privatlakareEntity.setVardgivareNamn(registration.getVerksamhetensNamn());

    /*
     * Effectively change the oneToMany cardinality of the following to act as oneToOne, see javadoc for more info
     */
    privatlakareEntity.updateBefattningar(registration.getBefattning());
    privatlakareEntity.updateVardformer(registration.getVardform());
    privatlakareEntity.updateVerksamhetstyper(registration.getVerksamhetstyp());
  }

}
