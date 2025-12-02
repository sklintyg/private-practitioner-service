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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatepractitionerservice.application.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.HospPerson;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.RegistrationStatus;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcHelper;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MonitoringLogService;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.HospPersonConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.HospUppdateringEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospUppdateringEntityRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatlakareEntityRepository;
import se.inera.intyg.privatepractitionerservice.integration.api.hosp.HospService;

/**
 * Created by pebe on 2015-09-04.
 */
@ExtendWith(MockitoExtension.class)
class HospUpdateServiceImplTest {


  private static final String PERSON_ID = "1912121212";
  private static final String PERSONAL_PRESCRIPTION_CODE = "7654321";
  private static final String PERSON_ID2 = "PERSON_ID2";
  private static final String PERSON_ID3 = "PERSON_ID3";
  @Mock
  private MdcHelper mdcHelper;

  @Mock
  private HospPersonService hospPersonService;

  @Mock
  private PrivatlakareEntityRepository privatlakareEntityRepository;

  @Mock
  private HospUppdateringEntityRepository hospUppdateringEntityRepository;

  @Mock
  private HospService hospService;

  @Mock
  private HospPersonConverter hospPersonConverter;

  @Mock
  private MailService mailService;

  @Mock
  private MonitoringLogService monitoringLogService;

  @InjectMocks
  private HospUpdateService hospUpdateService = new HospUpdateServiceImpl();

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(hospUpdateService, "mailInterval", 14400);
    ReflectionTestUtils.setField(hospUpdateService, "numberOfEmails", 3);
    lenient().doReturn("traceId").when(mdcHelper).traceId();
    lenient().doReturn("spanId").when(mdcHelper).spanId();
  }

  @Test
  void testUpdateHospInformationKanEjKontaktaHSA() {
    when(hospPersonService.getHospLastUpdate()).thenThrow(
        new IllegalStateException("Could not send message"));

    hospUpdateService.scheduledUpdateHospInformation();

    // Om det går att hämta senaste tidpunkt för hospupdate görs inget mer nu.
    // Ett nytt försök kommer göras vid nästa schemalagda körning.
    verify(hospPersonService).getHospLastUpdate();
    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  void testUpdateHospInformation() {

    HospUppdateringEntity hospUppdateringEntity = new HospUppdateringEntity();
    hospUppdateringEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringEntityRepository.findSingle()).thenReturn(hospUppdateringEntity);
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-05").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(true);
    PrivatlakareEntity privatlakareEntity2 = new PrivatlakareEntity();
    privatlakareEntity2.setPersonId(PERSON_ID2);
    privatlakareEntity2.setGodkandAnvandare(true);
    PrivatlakareEntity privatlakareEntity3 = new PrivatlakareEntity();
    privatlakareEntity3.setPersonId(PERSON_ID3);
    privatlakareEntity3.setGodkandAnvandare(true);
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    list.add(privatlakareEntity2);
    list.add(privatlakareEntity3);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // Om det går fel vid kontakt med hsa ska uppdateringsrutinen ändå fortsätta med nästa i listan.
    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new IllegalStateException("Could not send message"));
    HospPerson hospPersonResponse2 = createGetHospPersonResponse();
    hospPersonResponse2.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("DT", "Dietist"));
    hospPersonResponse2.getSpecialities().add(new Speciality("12", "Specialitet"));
    when(hospPersonService.getHospPerson(PERSON_ID2)).thenReturn(Optional.of(hospPersonResponse2));
    HospPerson hospPersonResponse3 = createGetHospPersonResponse();
    hospPersonResponse3.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("LK", "Läkare"));
    hospPersonResponse3.getSpecialities().add(new Speciality("12", "Specialitet"));
    when(hospPersonService.getHospPerson(PERSON_ID3)).thenReturn(Optional.of(hospPersonResponse3));

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdateringEntity.getSenasteHospUppdatering());
    verify(hospUppdateringEntityRepository).save(hospUppdateringEntity);

    // privatlakare2 and privatlakare3 should be updated with new hospinformation
    verify(privatlakareEntityRepository, times(0)).save(privatlakareEntity1);
    verify(privatlakareEntityRepository).save(privatlakareEntity2);
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.NOT_AUTHORIZED,
        privatlakareEntity2.getEpost());
    verify(privatlakareEntityRepository).save(privatlakareEntity3);
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED,
        privatlakareEntity3.getEpost());
  }

  @Test
  void testUpdateHospInformationEjGodkandAnvandare() {
    HospUppdateringEntity hospUppdateringEntity = new HospUppdateringEntity();
    hospUppdateringEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringEntityRepository.findSingle()).thenReturn(hospUppdateringEntity);
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-05").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(false);
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // privatlakare1 får nu läkarbehörighet men har fått GODKAND_ANVANDARE false innan
    HospPerson hospPersonResponse1 = createGetHospPersonResponse();
    hospPersonResponse1.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("LK", "Läkare"));
    hospPersonResponse1.getSpecialities().add(new Speciality("12", "Specialitet"));
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.of(hospPersonResponse1));

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdateringEntity.getSenasteHospUppdatering());
    verify(hospUppdateringEntityRepository).save(hospUppdateringEntity);

    // privatlakare1 should be updated with new hospinformation
    verify(privatlakareEntityRepository).save(privatlakareEntity1);
    // but should still be NOT_AUTHORIZED since GODKAND_ANVANDARE is false
    assertFalse(privatlakareEntity1.isGodkandAnvandare());
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.NOT_AUTHORIZED,
        privatlakareEntity1.getEpost());
  }

  @Test
  void testUpdateHospInformationKanEjKontaktaHSA1() {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new IllegalStateException("Could not send message"));

    assertThrows(HospUpdateFailedToContactHsaException.class,
        () -> hospUpdateService.updateHospInformation(privatlakareEntity, true)
    );

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  void testUpdateHospInformationKanEjKontaktaHSA2() {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);

    when(hospPersonService.addToCertifier(eq(PERSON_ID), nullable(String.class)))
        .thenThrow(new IllegalStateException("Could not send message"));

    assertThrows(HospUpdateFailedToContactHsaException.class,
        () -> hospUpdateService.updateHospInformation(privatlakareEntity, true)
    );

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  void testUpdateHospInformationEjLakare() throws HospUpdateFailedToContactHsaException {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(
        Optional.ofNullable(createGetHospPersonResponse()));

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakareEntity, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(RegistrationStatus.NOT_AUTHORIZED, response);
  }

  @Test
  void testUpdateHospInformationEjIHosp() throws HospUpdateFailedToContactHsaException {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());

    when(hospPersonService.addToCertifier(eq(PERSON_ID), nullable(String.class))).thenReturn(true);

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakareEntity, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(RegistrationStatus.WAITING_FOR_HOSP, response);
  }

  @Test
  void testUpdateHospInformationLakare() throws HospUpdateFailedToContactHsaException {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("LK", "Läkare"));
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.of(hospPersonResponse));

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakareEntity, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(RegistrationStatus.AUTHORIZED, response);
  }

  @Test
  void testUpdateHospInformationLakareEjGodkandAnvandare()
      throws HospUpdateFailedToContactHsaException {

    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(false);
    privatlakareEntity.setPersonId(PERSON_ID);

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("LK", "Läkare"));
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.of(hospPersonResponse));

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakareEntity, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(RegistrationStatus.NOT_AUTHORIZED, response);
  }

  @Test
  void testCheckForUpdatedHospInformationNotUpdated() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-01").atStartOfDay());

    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    verify(hospPersonService).getHospLastUpdate();
    verifyNoMoreInteractions(hospPersonService);
    verifyNoMoreInteractions(privatlakareEntityRepository);
  }

  @Test
  void testCheckForUpdatedHospInformationUpdated() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getLicensedHealthcareProfessions()
        .add(new LicensedHealtcareProfession("DT", "Dietist"));
    hospPersonResponse.getSpecialities().add(new Speciality("12", "Specialitet"));
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.of(hospPersonResponse));

    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verify(privatlakareEntityRepository).save(privatlakareEntity);

    assertEquals(LocalDate.parse("2015-09-05").atStartOfDay(),
        privatlakareEntity.getSenasteHospUppdatering());
    assertEquals(1, privatlakareEntity.getLegitimeradeYrkesgrupper().size());
    LegitimeradYrkesgruppEntity l = privatlakareEntity.getLegitimeradeYrkesgrupper().iterator()
        .next();
    assertEquals("DT", l.getKod());
    assertEquals("Dietist", l.getNamn());
    SpecialitetEntity s = privatlakareEntity.getSpecialiteter().getFirst();
    assertEquals("12", s.getKod());
    assertEquals("Specialitet", s.getNamn());
    assertEquals(PERSONAL_PRESCRIPTION_CODE, privatlakareEntity.getForskrivarKod());
  }

  @Test
  void testCheckForUpdatedHospInformationKanEjKontaktaHSA1() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenThrow(
        new IllegalStateException("Could not send message"));
    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakareEntity.getSenasteHospUppdatering());
  }

  @Test
  void testCheckForUpdatedHospInformationKanEjKontaktaHSA2() {
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new IllegalStateException("Could not send message"));

    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakareEntity.getSenasteHospUppdatering());
  }

  @Test
  void testCheckForUpdatedHospInformationTillbakadragenLakarbehorighet() {
    // Haft läkarbehörighet innan.
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setForskrivarKod("7777777");
    final List<LegitimeradYrkesgruppEntity> legitimeradYrkesgrupperEntity = new ArrayList<>();
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity("Läkare", "LK"));
    privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupperEntity);
    List<SpecialitetEntity> specialiteter = new ArrayList<>();
    specialiteter.add(new SpecialitetEntity("Specialitet", "12"));
    privatlakareEntity.setSpecialiteter(specialiteter);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    // Läkarbehörigheten är borttagen ur HSA
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());

    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verify(privatlakareEntityRepository).save(privatlakareEntity);

    assertEquals(LocalDate.parse("2015-09-05").atStartOfDay(),
        privatlakareEntity.getSenasteHospUppdatering());
    assertEquals(0, privatlakareEntity.getLegitimeradeYrkesgrupper().size());
    assertEquals(0, privatlakareEntity.getSpecialiteter().size());
    assertNull(privatlakareEntity.getForskrivarKod());
  }

  @Test
  void testCheckForUpdatedHospInformationTillbakadragenLakarbehorighetKanEjKontaktaHSA() {
    // Haft läkarbehörighet innan.
    PrivatlakareEntity privatlakareEntity = new PrivatlakareEntity();
    privatlakareEntity.setGodkandAnvandare(true);
    privatlakareEntity.setPersonId(PERSON_ID);
    privatlakareEntity.setForskrivarKod("7777777");
    final List<LegitimeradYrkesgruppEntity> legitimeradYrkesgrupperEntity = new ArrayList<>();
    legitimeradYrkesgrupperEntity.add(
        new LegitimeradYrkesgruppEntity("Läkare", "LK"));
    privatlakareEntity.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupperEntity);
    List<SpecialitetEntity> specialiteter = new ArrayList<>();
    specialiteter.add(new SpecialitetEntity("Specialitet", "12"));
    privatlakareEntity.setSpecialiteter(specialiteter);
    privatlakareEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    // Läkarbehörigheten är borttagen ur HSA
    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(new IllegalStateException());

    hospUpdateService.checkForUpdatedHospInformation(privatlakareEntity);

    // Om det ej gick att kontakta HSA ska datumet för lasthospupdate inte ändras.
    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verifyNoMoreInteractions(hospUppdateringEntityRepository);
    verifyNoMoreInteractions(privatlakareEntityRepository);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakareEntity.getSenasteHospUppdatering());
  }

  @Test
  void testAwaitingHospNotificationMailSentCorrectly() {
    ReflectionTestUtils.setField(hospUpdateService, "lastUpdate",
        LocalDateTime.now().minusMinutes(15000));
    HospUppdateringEntity hospUppdateringEntity = new HospUppdateringEntity();
    hospUppdateringEntity.setSenasteHospUppdatering(LocalDate.now().minusDays(15).atStartOfDay());
    when(hospUppdateringEntityRepository.findSingle()).thenReturn(hospUppdateringEntity);

    LocalDateTime hospLastUpdate = LocalDate.now().atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(true);
    privatlakareEntity1.setRegistreringsdatum(LocalDate.now().minusDays(10).atStartOfDay());
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdateringEntity.getSenasteHospUppdatering());
    verify(hospUppdateringEntityRepository).save(hospUppdateringEntity);

    // Since registreringsdatum is 10 or more days before the last hospUpdate, a mail should be sent
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.WAITING_FOR_HOSP,
        privatlakareEntity1.getEpost());
  }

  @Test
  void testAwaitingHospNotificationMailNotSentBeforeAllottedTime() {
    HospUppdateringEntity hospUppdateringEntity = new HospUppdateringEntity();
    hospUppdateringEntity.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringEntityRepository.findSingle()).thenReturn(hospUppdateringEntity);

    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(true);
    // Set this within ten days of last hosp update
    privatlakareEntity1.setRegistreringsdatum(LocalDate.parse("2015-09-10").atStartOfDay());
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdateringEntity.getSenasteHospUppdatering());
    verify(hospUppdateringEntityRepository).save(hospUppdateringEntity);

    // Since registreringsdatum is less than 10 days before the last hospUpdate, a mail should not be sent
    verify(mailService, times(0)).sendRegistrationStatusEmail(RegistrationStatus.WAITING_FOR_HOSP,
        privatlakareEntity1.getEpost());
  }

  @Test
  void testRegistrationRemovedAfterCorrectTimePeriod() {
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(true);
    privatlakareEntity1.setRegistreringsdatum(LocalDate.now().minusDays(30).atStartOfDay());
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());
    when(hospPersonService.removeFromCertifier(eq(PERSON_ID), nullable(String.class),
        anyString())).thenReturn(true);

    hospUpdateService.scheduledUpdateHospInformation();

    verify(mailService).sendRegistrationRemovedEmail(privatlakareEntity1);
    verify(monitoringLogService).logRegistrationRemoved(privatlakareEntity1.getPersonId(),
        privatlakareEntity1.getHsaId());
    verify(privatlakareEntityRepository).delete(privatlakareEntity1);
    verify(hospPersonService).removeFromCertifier(eq(PERSON_ID), nullable(String.class),
        anyString());
  }

  @Test
  void testRegistrationNotRemovedWhileInAllowedTimeFrame() {
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    ReflectionTestUtils.setField(hospUpdateService, "lastUpdate",
        LocalDate.parse("2015-09-15").atStartOfDay());
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    PrivatlakareEntity privatlakareEntity1 = new PrivatlakareEntity();
    privatlakareEntity1.setPersonId(PERSON_ID);
    privatlakareEntity1.setGodkandAnvandare(true);
    privatlakareEntity1.setRegistreringsdatum(LocalDate.now().minusDays(29).atStartOfDay());
    ArrayList<PrivatlakareEntity> list = new ArrayList<>();
    list.add(privatlakareEntity1);
    when(privatlakareEntityRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(Optional.empty());

    hospUpdateService.scheduledUpdateHospInformation();

    verify(mailService, times(0)).sendRegistrationRemovedEmail(privatlakareEntity1);
    verify(privatlakareEntityRepository, times(0)).delete(privatlakareEntity1);
    verify(hospPersonService, times(0)).removeFromCertifier(anyString(), anyString(), anyString());
  }

  private HospPerson createGetHospPersonResponse() {
    return HospPerson.builder()
        .personalPrescriptionCode(PERSONAL_PRESCRIPTION_CODE)
        .licensedHealthcareProfessions(new ArrayList<>())
        .specialities(new ArrayList<>())
        .restrictions(new ArrayList<>())
        .build();
  }
}
