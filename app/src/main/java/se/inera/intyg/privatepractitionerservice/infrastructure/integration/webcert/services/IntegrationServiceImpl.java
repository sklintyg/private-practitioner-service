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
package se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.web.controller.internalapi.dto.ValidatePrivatePractitionerResultCode;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.hsa.services.HospUpdateService;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.kodverk.Befattningar;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.kodverk.Vardformer;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.kodverk.Verksamhetstyper;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.ArbetsplatsKodDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.BefattningDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.CareProviderDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.CvDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.EnhetsTyp;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.GeografiskIndelningDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.GetPrivatePractitionerResponseDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.HoSPersonDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.HsaIdDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.LegitimeradYrkesgruppDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.PersonIdDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.ResultCodeEnum;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.SpecialitetDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.integration.webcert.services.dto.VerksamhetDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Befattning;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.LegitimeradYrkesgrupp;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Privatlakare;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Specialitet;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Vardform;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Verksamhetstyp;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.service.DateHelperService;
import se.inera.intyg.privatepractitionerservice.infrastructure.utils.PrivatlakareUtils;

/**
 * Created by pebe on 2015-08-17.
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {

  public static final String NO_PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_EXISTS =
      "No private practitioner with personal identity number: %s exists.";
  private static final String PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_IS_NOT_AUTHORIZED =
      "Private practitioner with personal identity number: %s is not authorized to use webcert.";

  private final PrivatlakareRepository privatlakareRepository;
  private final HospUpdateService hospUpdateService;
  private final DateHelperService dateHelperService;
  private final HashUtility hashUtility;

  @Autowired
  public IntegrationServiceImpl(PrivatlakareRepository privatlakareRepository,
      HospUpdateService hospUpdateService, DateHelperService dateHelperService,
      HashUtility hashUtility) {
    this.privatlakareRepository = privatlakareRepository;
    this.hospUpdateService = hospUpdateService;
    this.dateHelperService = dateHelperService;
    this.hashUtility = hashUtility;
  }

  @Override
  @Transactional
  public GetPrivatePractitionerResponseDTO getPrivatePractitionerByHsaId(String personHsaId) {

    final var response = new GetPrivatePractitionerResponseDTO();

    Privatlakare privatlakare = privatlakareRepository.findByHsaId(personHsaId);

    if (privatlakare == null) {
      response.setHoSPerson(null);
      response.setResultCode(ResultCodeEnum.ERROR);
      response.setResultText("No private practitioner with hsa id: " + personHsaId + " exists.");
    } else {
      response.setResultCode(ResultCodeEnum.OK);
      checkFirstLogin(privatlakare);
      convertPrivatlakareToResponse(privatlakare, response);
    }

    return response;
  }

  @Override
  @Transactional
  public GetPrivatePractitionerResponseDTO getPrivatePractitionerByPersonId(
      String personalIdentityNumber) {

    final var response = new GetPrivatePractitionerResponseDTO();

    Privatlakare privatlakare = privatlakareRepository.findByPersonId(personalIdentityNumber);

    if (privatlakare == null) {
      response.setHoSPerson(null);
      response.setResultCode(ResultCodeEnum.ERROR);
      response.setResultText(errorTextForMissingPractitioner(personalIdentityNumber));
    } else {
      response.setResultCode(ResultCodeEnum.OK);
      checkFirstLogin(privatlakare);
      convertPrivatlakareToResponse(privatlakare, response);
    }

    return response;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public ValidatePrivatePractitionerResponse validatePrivatePractitionerByPersonId(
      String personalIdentityNumber) {

    ValidatePrivatePractitionerResponse response = new ValidatePrivatePractitionerResponse();

    Privatlakare privatlakare = privatlakareRepository.findByPersonId(personalIdentityNumber);

    if (privatlakare == null) {
      response.setResultCode(ValidatePrivatePractitionerResultCode.NO_ACCOUNT);
      response.setResultText(errorTextForMissingPractitioner(personalIdentityNumber));
    } else {
      hospUpdateService.checkForUpdatedHospInformation(privatlakare);
      if (privatlakare.isGodkandAnvandare() && PrivatlakareUtils.hasLakareLegitimation(
          privatlakare)) {
        response.setResultCode(ValidatePrivatePractitionerResultCode.OK);
        // Check if this is the first time the user logins to Webcert after getting godkand status
        checkFirstLogin(privatlakare);
      } else {
        response.setResultCode(ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP);
        response.setResultText(errorTextForNotAuthorizedPractitioner(personalIdentityNumber));
      }
    }

    return response;
  }

  private void checkFirstLogin(Privatlakare privatlakare) {
    LocalDateTime localDateTime = dateHelperService.now();
    // If startdatum is not set, set current time as startdatum for privatlakare
    if (privatlakare.getEnhetStartdatum() == null
        || privatlakare.getVardgivareStartdatum() == null) {
      privatlakare.setEnhetStartdatum(localDateTime);
      privatlakare.setVardgivareStartdatum(localDateTime);
      privatlakareRepository.save(privatlakare);
    }
  }

  private static final String ARBETSPLATSKOD_ROOT = "1.2.752.29.4.71";
  private static final String HSAID_ROOT = "1.2.752.129.2.1.4.1";
  private static final String PERSONID_ROOT = "1.2.752.129.2.1.3.1";

  private HsaIdDTO convertToHsaId(String ext) {
    if (ext == null) {
      return null;
    }
    final var hsaId = new HsaIdDTO();
    hsaId.setRoot(HSAID_ROOT);
    hsaId.setExtension(ext);
    return hsaId;
  }

  private ArbetsplatsKodDTO convertToArbetsplatsKod(String ext) {
    if (ext == null) {
      return null;
    }
    final var arbetsplatsKod = new ArbetsplatsKodDTO();
    arbetsplatsKod.setRoot(ARBETSPLATSKOD_ROOT);
    arbetsplatsKod.setExtension(ext);
    return arbetsplatsKod;
  }

  private PersonIdDTO convertToPersonId(String ext) {
    if (ext == null) {
      return null;
    }
    final var personId = new PersonIdDTO();
    personId.setRoot(PERSONID_ROOT);
    personId.setExtension(ext);
    return personId;
  }

  private void convertPrivatlakareToResponse(Privatlakare privatlakare,
      GetPrivatePractitionerResponseDTO response) {
    final var hoSPersonType = new HoSPersonDTO();

    final var vardgivareType = new CareProviderDTO();
    vardgivareType.setSlutdatum(privatlakare.getVardgivareSlutdatum());
    vardgivareType.setStartdatum(privatlakare.getVardgivareStartdatum());
    vardgivareType.setVardgivareId(convertToHsaId(privatlakare.getVardgivareId()));
    vardgivareType.setVardgivarenamn(privatlakare.getVardgivareNamn());

    final var geografiskIndelningType = new GeografiskIndelningDTO();
    final var kommunCv = new CvDTO();
    kommunCv.setDisplayName(privatlakare.getKommun());
    geografiskIndelningType.setKommun(kommunCv);
    final var lanCv = new CvDTO();
    lanCv.setDisplayName(privatlakare.getLan());
    geografiskIndelningType.setLan(lanCv);

    final var enhetType = new EnhetsTyp();
    enhetType.setAgarform(privatlakare.getAgarform());
    enhetType.setArbetsplatskod(convertToArbetsplatsKod(privatlakare.getArbetsplatsKod()));
    enhetType.setEnhetsId(convertToHsaId(privatlakare.getEnhetsId()));
    enhetType.setEnhetsnamn(privatlakare.getEnhetsNamn());
    enhetType.setEpost(privatlakare.getEpost());
    enhetType.setGeografiskIndelning(geografiskIndelningType);
    enhetType.setPostadress(privatlakare.getPostadress());
    enhetType.setPostnummer(privatlakare.getPostnummer());
    enhetType.setPostort(privatlakare.getPostort());
    enhetType.setSlutdatum(privatlakare.getEnhetSlutDatum());
    enhetType.setStartdatum(privatlakare.getEnhetStartdatum());
    enhetType.setTelefonnummer(privatlakare.getTelefonnummer());
    enhetType.setVardgivare(vardgivareType);

    final var verksamhetType = new VerksamhetDTO();
    if (privatlakare.getVardformer() != null) {
      for (Vardform vardform : privatlakare.getVardformer()) {

        final var vardformCv = new CvDTO();
        vardformCv.setCodeSystemName(Vardformer.VARDFORM_NAME);
        vardformCv.setCodeSystemVersion(Vardformer.VARDFORM_VERSION);
        vardformCv.setCodeSystem(Vardformer.VARDFORM_OID);
        vardformCv.setCode(vardform.getKod());
        vardformCv.setDisplayName(Vardformer.getDisplayName(vardform.getKod()));

        verksamhetType.getVardform().add(vardformCv);
      }
    }
    if (privatlakare.getVerksamhetstyper() != null) {
      for (Verksamhetstyp verksamhetstyp : privatlakare.getVerksamhetstyper()) {

        final var verksamhetstypCv = new CvDTO();
        verksamhetstypCv.setCodeSystemName(Verksamhetstyper.VERKSAMHETSTYP_NAME);
        verksamhetstypCv.setCodeSystemVersion(Verksamhetstyper.VERKSAMHETSTYP_VERSION);
        verksamhetstypCv.setCodeSystem(Verksamhetstyper.VERKSAMHETSTYP_OID);
        verksamhetstypCv.setCode(verksamhetstyp.getKod());
        verksamhetstypCv.setDisplayName(Verksamhetstyper.getDisplayName(verksamhetstyp.getKod()));

        verksamhetType.getVerksamhet().add(verksamhetstypCv);
      }
    }
    enhetType.setVerksamhetstyp(verksamhetType);

    hoSPersonType.setEnhet(enhetType);
    hoSPersonType.setForskrivarkod(privatlakare.getForskrivarKod());
    hoSPersonType.setFullstandigtNamn(privatlakare.getFullstandigtNamn());
    hoSPersonType.setGodkandAnvandare(privatlakare.isGodkandAnvandare());
    hoSPersonType.setHsaId(convertToHsaId(privatlakare.getHsaId()));
    hoSPersonType.setPersonId(convertToPersonId(privatlakare.getPersonId()));

    if (privatlakare.getBefattningar() != null) {
      for (Befattning befattning : privatlakare.getBefattningar()) {

        final var befattningType = new BefattningDTO();
        befattningType.setKod(befattning.getKod());
        befattningType.setNamn(Befattningar.getDisplayName(befattning.getKod()));

        hoSPersonType.getBefattning().add(befattningType);
      }
    }

    if (privatlakare.getLegitimeradeYrkesgrupper() != null) {
      for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : privatlakare.getLegitimeradeYrkesgrupper()) {
        final var legitimeradYrkesgruppType = new LegitimeradYrkesgruppDTO();
        legitimeradYrkesgruppType.setKod(legitimeradYrkesgrupp.getKod());
        legitimeradYrkesgruppType.setNamn(legitimeradYrkesgrupp.getNamn());
        hoSPersonType.getLegitimeradYrkesgrupp().add(legitimeradYrkesgruppType);
      }
    }

    if (privatlakare.getSpecialiteter() != null) {
      for (Specialitet specialitet : privatlakare.getSpecialiteter()) {

        final var specialitetType = new SpecialitetDTO();
        specialitetType.setKod(specialitet.getKod());
        specialitetType.setNamn(specialitet.getNamn());

        hoSPersonType.getSpecialitet().add(specialitetType);
      }
    }

    response.setHoSPerson(hoSPersonType);
  }

  private String errorTextForNotAuthorizedPractitioner(String personalIdentityNumber) {
    return getMessageWithHashedPersonalId(
        PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_IS_NOT_AUTHORIZED, personalIdentityNumber);
  }

  private String errorTextForMissingPractitioner(String personalIdentityNumber) {
    return getMessageWithHashedPersonalId(NO_PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_EXISTS,
        personalIdentityNumber);
  }

  private String getMessageWithHashedPersonalId(String message, String personalIdentityNumber) {
    return String.format(message, getPersonalIdentityNumberHash(personalIdentityNumber));
  }

  private String getPersonalIdentityNumberHash(String personalIdentityNumber) {
    return hashUtility.hash(personalIdentityNumber);
  }
}
