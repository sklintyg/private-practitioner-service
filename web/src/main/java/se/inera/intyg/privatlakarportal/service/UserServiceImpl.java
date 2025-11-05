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
package se.inera.intyg.privatlakarportal.service;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.infra.pu.integration.api.services.PUService;
import se.inera.intyg.privatlakarportal.auth.PrivatlakarUser;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.common.utils.PrivatlakareUtils;
import se.inera.intyg.privatlakarportal.logging.HashUtility;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.model.User;
import se.inera.intyg.schemas.contract.Personnummer;

@Service
@Slf4j
@Transactional(transactionManager = "transactionManager")
public class UserServiceImpl implements UserService {

  @Autowired
  private PrivatlakareRepository privatlakareRepository;

  @Autowired
  private PUService puService;

  @Autowired
  private HashUtility hashUtility;

  @Override
  public PrivatlakarUser getUser() {
   return null;
  }

  @Override
  public User getUserWithStatus() {
    PrivatlakarUser privatlakarUser = getUser();

    if (privatlakarUser == null) {
      throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.NOT_FOUND,
          "No logged in user");
    }

    RegistrationStatus status;
    Privatlakare privatlakare = privatlakareRepository.findByPersonId(
        privatlakarUser.getPersonalIdentityNumber());
    if (privatlakare == null) {
      status = RegistrationStatus.NOT_STARTED;
    } else if (!privatlakare.isGodkandAnvandare()) {
      status = RegistrationStatus.NOT_AUTHORIZED;
    } else if (!PrivatlakareUtils.hasLakareLegitimation(privatlakare)) {
      status = RegistrationStatus.WAITING_FOR_HOSP;
    } else {
      status = RegistrationStatus.AUTHORIZED;
    }

    boolean nameUpdated = false;
    PersonSvar.Status personSvarStatus;
    try {
      Optional<Personnummer> personnummerOptional = Personnummer
          .createPersonnummer(privatlakarUser.getPersonalIdentityNumber());
      final var personalIdHash = hashUtility.hash(privatlakarUser.getPersonalIdentityNumber());

      if (personnummerOptional.isPresent()) {
        PersonSvar personSvar = puService.getPerson(personnummerOptional.get());
        personSvarStatus = personSvar.getStatus();

        if (personSvar.getStatus() == PersonSvar.Status.FOUND && personSvar.getPerson() != null) {
          String name = personSvar.getPerson().fornamn() + " " + personSvar.getPerson().efternamn();
          privatlakarUser.updateNameFromPuService(name);

          // Check if name has changed and update in database
          if (privatlakare != null && !name.equals(privatlakare.getFullstandigtNamn())) {
            log.info("Updated name for user '{}'", personalIdHash);
            privatlakare.setFullstandigtNamn(name);
            privatlakareRepository.save(privatlakare);
            nameUpdated = true;
          }

        } else if (personSvar.getStatus() == PersonSvar.Status.NOT_FOUND) {
          log.warn("Person '{}' not found in puService", personalIdHash);
        } else {
          log.error("puService returned error status for personId '{}'", personalIdHash);
        }
      } else {
        log.error(
            "puService could not parse personnummer, returning error status for personId '{}'",
            personalIdHash);
        personSvarStatus = PersonSvar.Status.ERROR;
      }
    } catch (RuntimeException e) {
      log.error("Failed to contact puService", e);
      personSvarStatus = PersonSvar.Status.ERROR;
    }

    return new User(privatlakarUser, personSvarStatus, status, nameUpdated);
  }

}
