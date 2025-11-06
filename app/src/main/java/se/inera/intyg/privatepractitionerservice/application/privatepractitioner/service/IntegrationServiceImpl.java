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

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResponse;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ValidatePrivatePractitionerResultCode;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.Befattningar;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.Vardformer;
import se.inera.intyg.privatepractitionerservice.infrastructure.codesystem.Verksamhetstyper;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ArbetsplatsKodDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.BefattningDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CareProviderDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CvDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.EnhetsTyp;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GeografiskIndelningDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.GetPrivatePractitionerResponseDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.HoSPersonDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.HsaIdDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.LegitimeradYrkesgruppDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PersonIdDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.ResultCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.SpecialitetDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.VerksamhetDTO;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.BefattningEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VardformEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.VerksamhetstypEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.util.PrivatlakareUtils;

/**
 * Created by pebe on 2015-08-17.
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {

  public static final String NO_PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_EXISTS =
      "No private practitioner with personal identity number: %s exists.";
  private static final String PRACTITIONER_WITH_PERSONAL_IDENTITY_NUMBER_IS_NOT_AUTHORIZED =
      "Private practitioner with personal identity number: %s is not authorized to use webcert.";

  private final PrivatlakareEntityRepository privatlakareEntityRepository;
  private final HospUpdateService hospUpdateService;
  private final DateHelperService dateHelperService;
  private final HashUtility hashUtility;

  @Autowired
  public IntegrationServiceImpl(PrivatlakareEntityRepository privatlakareEntityRepository,
      HospUpdateService hospUpdateService, DateHelperService dateHelperService,
      HashUtility hashUtility) {
    this.privatlakareEntityRepository = privatlakareEntityRepository;
    this.hospUpdateService = hospUpdateService;
    this.dateHelperService = dateHelperService;
    this.hashUtility = hashUtility;
  }

  @Override
  @Transactional
  public GetPrivatePractitionerResponseDTO getPrivatePractitionerByHsaId(String personHsaId) {

    final var response = new GetPrivatePractitionerResponseDTO();

    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByHsaId(personHsaId);

    if (privatlakareEntity == null) {
      response.setHoSPerson(null);
      response.setResultCode(ResultCodeEnum.ERROR);
      response.setResultText("No private practitioner with hsa id: " + personHsaId + " exists.");
    } else {
      response.setResultCode(ResultCodeEnum.OK);
      checkFirstLogin(privatlakareEntity);
      convertPrivatlakareToResponse(privatlakareEntity, response);
    }

    return response;
  }

  @Override
  @Transactional
  public GetPrivatePractitionerResponseDTO getPrivatePractitionerByPersonId(
      String personalIdentityNumber) {

    final var response = new GetPrivatePractitionerResponseDTO();

    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(
        personalIdentityNumber);

    if (privatlakareEntity == null) {
      response.setHoSPerson(null);
      response.setResultCode(ResultCodeEnum.ERROR);
      response.setResultText(errorTextForMissingPractitioner(personalIdentityNumber));
    } else {
      response.setResultCode(ResultCodeEnum.OK);
      checkFirstLogin(privatlakareEntity);
      convertPrivatlakareToResponse(privatlakareEntity, response);
    }

    return response;
  }

  @Override
  @Transactional(transactionManager = "transactionManager")
  public ValidatePrivatePractitionerResponse validatePrivatePractitionerByPersonId(
      String personalIdentityNumber) {

    ValidatePrivatePractitionerResponse response = new ValidatePrivatePractitionerResponse();

    PrivatlakareEntity privatlakareEntity = privatlakareEntityRepository.findByPersonId(
        personalIdentityNumber);

    if (privatlakareEntity == null) {
      response.setResultCode(ValidatePrivatePractitionerResultCode.NO_ACCOUNT);
      response.setResultText(errorTextForMissingPractitioner(personalIdentityNumber));
    } else {
      hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);
      if (privatlakareEntity.isGodkandAnvandare() && PrivatlakareUtils.hasLakareLegitimation(
          privatlakareEntity)) {
        response.setResultCode(ValidatePrivatePractitionerResultCode.OK);
        // Check if this is the first time the user logins to Webcert after getting godkand status
        checkFirstLogin(privatlakareEntity);
      } else {
        response.setResultCode(ValidatePrivatePractitionerResultCode.NOT_AUTHORIZED_IN_HOSP);
        response.setResultText(errorTextForNotAuthorizedPractitioner(personalIdentityNumber));
      }
    }

    return response;
  }

  private void checkFirstLogin(PrivatlakareEntity privatlakareEntity) {
    LocalDateTime localDateTime = dateHelperService.now();
    // If startdatum is not set, set current time as startdatum for privatlakare
    if (privatlakareEntity.getEnhetStartdatum() == null
        || privatlakareEntity.getVardgivareStartdatum() == null) {
      privatlakareEntity.setEnhetStartdatum(localDateTime);
      privatlakareEntity.setVardgivareStartdatum(localDateTime);
      privatlakareEntityRepository.save(privatlakareEntity);
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

  private void convertPrivatlakareToResponse(PrivatlakareEntity privatlakareEntity,
      GetPrivatePractitionerResponseDTO response) {
    final var hoSPersonType = new HoSPersonDTO();

    final var vardgivareType = new CareProviderDTO();
    vardgivareType.setSlutdatum(privatlakareEntity.getVardgivareSlutdatum());
    vardgivareType.setStartdatum(privatlakareEntity.getVardgivareStartdatum());
    vardgivareType.setVardgivareId(convertToHsaId(privatlakareEntity.getVardgivareId()));
    vardgivareType.setVardgivarenamn(privatlakareEntity.getVardgivareNamn());

    final var geografiskIndelningType = new GeografiskIndelningDTO();
    final var kommunCv = new CvDTO();
    kommunCv.setDisplayName(privatlakareEntity.getKommun());
    geografiskIndelningType.setKommun(kommunCv);
    final var lanCv = new CvDTO();
    lanCv.setDisplayName(privatlakareEntity.getLan());
    geografiskIndelningType.setLan(lanCv);

    final var enhetType = new EnhetsTyp();
    enhetType.setAgarform(privatlakareEntity.getAgarform());
    enhetType.setArbetsplatskod(convertToArbetsplatsKod(privatlakareEntity.getArbetsplatsKod()));
    enhetType.setEnhetsId(convertToHsaId(privatlakareEntity.getEnhetsId()));
    enhetType.setEnhetsnamn(privatlakareEntity.getEnhetsNamn());
    enhetType.setEpost(privatlakareEntity.getEpost());
    enhetType.setGeografiskIndelning(geografiskIndelningType);
    enhetType.setPostadress(privatlakareEntity.getPostadress());
    enhetType.setPostnummer(privatlakareEntity.getPostnummer());
    enhetType.setPostort(privatlakareEntity.getPostort());
    enhetType.setSlutdatum(privatlakareEntity.getEnhetSlutDatum());
    enhetType.setStartdatum(privatlakareEntity.getEnhetStartdatum());
    enhetType.setTelefonnummer(privatlakareEntity.getTelefonnummer());
    enhetType.setVardgivare(vardgivareType);

    final var verksamhetType = new VerksamhetDTO();
    if (privatlakareEntity.getVardformer() != null) {
      for (VardformEntity vardformEntity : privatlakareEntity.getVardformer()) {

        final var vardformCv = new CvDTO();
        vardformCv.setCodeSystemName(Vardformer.VARDFORM_NAME);
        vardformCv.setCodeSystemVersion(Vardformer.VARDFORM_VERSION);
        vardformCv.setCodeSystem(Vardformer.VARDFORM_OID);
        vardformCv.setCode(vardformEntity.getKod());
        vardformCv.setDisplayName(Vardformer.getDisplayName(vardformEntity.getKod()));

        verksamhetType.getVardform().add(vardformCv);
      }
    }
    if (privatlakareEntity.getVerksamhetstyper() != null) {
      for (VerksamhetstypEntity verksamhetstypEntity : privatlakareEntity.getVerksamhetstyper()) {

        final var verksamhetstypCv = new CvDTO();
        verksamhetstypCv.setCodeSystemName(Verksamhetstyper.VERKSAMHETSTYP_NAME);
        verksamhetstypCv.setCodeSystemVersion(Verksamhetstyper.VERKSAMHETSTYP_VERSION);
        verksamhetstypCv.setCodeSystem(Verksamhetstyper.VERKSAMHETSTYP_OID);
        verksamhetstypCv.setCode(verksamhetstypEntity.getKod());
        verksamhetstypCv.setDisplayName(
            Verksamhetstyper.getDisplayName(verksamhetstypEntity.getKod()));

        verksamhetType.getVerksamhet().add(verksamhetstypCv);
      }
    }
    enhetType.setVerksamhetstyp(verksamhetType);

    hoSPersonType.setEnhet(enhetType);
    hoSPersonType.setForskrivarkod(privatlakareEntity.getForskrivarKod());
    hoSPersonType.setFullstandigtNamn(privatlakareEntity.getFullstandigtNamn());
    hoSPersonType.setGodkandAnvandare(privatlakareEntity.isGodkandAnvandare());
    hoSPersonType.setHsaId(convertToHsaId(privatlakareEntity.getHsaId()));
    hoSPersonType.setPersonId(convertToPersonId(privatlakareEntity.getPersonId()));

    if (privatlakareEntity.getBefattningar() != null) {
      for (BefattningEntity befattningEntity : privatlakareEntity.getBefattningar()) {

        final var befattningType = new BefattningDTO();
        befattningType.setKod(befattningEntity.getKod());
        befattningType.setNamn(Befattningar.getDisplayName(befattningEntity.getKod()));

        hoSPersonType.getBefattning().add(befattningType);
      }
    }

    if (privatlakareEntity.getLegitimeradeYrkesgrupper() != null) {
      for (LegitimeradYrkesgruppEntity legitimeradYrkesgruppEntity : privatlakareEntity.getLegitimeradeYrkesgrupper()) {
        final var legitimeradYrkesgruppType = new LegitimeradYrkesgruppDTO();
        legitimeradYrkesgruppType.setKod(legitimeradYrkesgruppEntity.getKod());
        legitimeradYrkesgruppType.setNamn(legitimeradYrkesgruppEntity.getNamn());
        hoSPersonType.getLegitimeradYrkesgrupp().add(legitimeradYrkesgruppType);
      }
    }

    if (privatlakareEntity.getSpecialiteter() != null) {
      for (SpecialitetEntity specialitetEntity : privatlakareEntity.getSpecialiteter()) {

        final var specialitetType = new SpecialitetDTO();
        specialitetType.setKod(specialitetEntity.getKod());
        specialitetType.setNamn(specialitetEntity.getNamn());

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
