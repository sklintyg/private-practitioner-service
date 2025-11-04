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
package se.inera.intyg.privatlakarportal.hsa.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.xml.ws.WebServiceException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.common.service.MailService;
import se.inera.intyg.privatlakarportal.hsa.model.HospPerson;
import se.inera.intyg.privatlakarportal.hsa.monitoring.MonitoringLogService;
import se.inera.intyg.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatlakarportal.logging.MdcHelper;
import se.inera.intyg.privatlakarportal.persistence.model.HospUppdatering;
import se.inera.intyg.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.model.Specialitet;
import se.inera.intyg.privatlakarportal.persistence.repository.HospUppdateringRepository;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;

/**
 * Created by pebe on 2015-09-04.
 */
@RunWith(MockitoJUnitRunner.class)
public class HospUpdateServiceImplTest {

  private static final String PERSON_ID = "1912121212";
  private static final String PERSONAL_PRESCRIPTION_CODE = "7654321";
  private static final String PERSON_ID2 = "PERSON_ID2";
  private static final String PERSON_ID3 = "PERSON_ID3";
  @Mock
  private MdcHelper mdcHelper;

  @Mock
  private HospPersonService hospPersonService;

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @Mock
  private HospUppdateringRepository hospUppdateringRepository;

  @Mock
  private MailService mailService;

  @Mock
  private MonitoringLogService monitoringLogService;

  @InjectMocks
  private HospUpdateService hospUpdateService = new HospUpdateServiceImpl();

  @Before
  public void setup() {
    ReflectionTestUtils.setField(hospUpdateService, "mailInterval", 14400);
    ReflectionTestUtils.setField(hospUpdateService, "numberOfEmails", 3);
    doReturn("traceId").when(mdcHelper).traceId();
    doReturn("spanId").when(mdcHelper).spanId();
  }

  @Test
  public void testUpdateHospInformationKanEjKontaktaHSA()
      throws HospUpdateFailedToContactHsaException {
    when(hospPersonService.getHospLastUpdate()).thenThrow(
        new WebServiceException("Could not send message"));

    hospUpdateService.scheduledUpdateHospInformation();

    // Om det går att hämta senaste tidpunkt för hospupdate görs inget mer nu.
    // Ett nytt försök kommer göras vid nästa schemalagda körning.
    verify(hospPersonService).getHospLastUpdate();
    verifyNoMoreInteractions(hospPersonService);
  }

  @Test
  public void testUpdateHospInformation() {

    HospUppdatering hospUppdatering = new HospUppdatering();
    hospUppdatering.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringRepository.findSingle()).thenReturn(hospUppdatering);
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-05").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(true);
    Privatlakare privatlakare2 = new Privatlakare();
    privatlakare2.setPersonId(PERSON_ID2);
    privatlakare2.setGodkandAnvandare(true);
    Privatlakare privatlakare3 = new Privatlakare();
    privatlakare3.setPersonId(PERSON_ID3);
    privatlakare3.setGodkandAnvandare(true);
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    list.add(privatlakare2);
    list.add(privatlakare3);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // Om det går fel vid kontakt med hsa ska uppdateringsrutinen ändå fortsätta med nästa i listan.
    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new WebServiceException("Could not send message"));
    HospPerson hospPersonResponse2 = createGetHospPersonResponse();
    hospPersonResponse2.getTitleCodes().add("DT");
    hospPersonResponse2.getHsaTitles().add("Dietist");
    hospPersonResponse2.getSpecialityCodes().add("12");
    hospPersonResponse2.getSpecialityNames().add("Specialitet");
    when(hospPersonService.getHospPerson(PERSON_ID2)).thenReturn(hospPersonResponse2);
    HospPerson hospPersonResponse3 = createGetHospPersonResponse();
    hospPersonResponse3.getTitleCodes().add("LK");
    hospPersonResponse3.getHsaTitles().add("Läkare");
    hospPersonResponse3.getSpecialityCodes().add("12");
    hospPersonResponse3.getSpecialityNames().add("Specialitet");
    when(hospPersonService.getHospPerson(PERSON_ID3)).thenReturn(hospPersonResponse3);

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdatering.getSenasteHospUppdatering());
    verify(hospUppdateringRepository).save(hospUppdatering);

    // privatlakare2 and privatlakare3 should be updated with new hospinformation
    verify(privatlakareRepository, times(0)).save(privatlakare1);
    verify(privatlakareRepository).save(privatlakare2);
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.NOT_AUTHORIZED,
        privatlakare2);
    verify(privatlakareRepository).save(privatlakare3);
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.AUTHORIZED, privatlakare3);
  }

  @Test
  public void testUpdateHospInformationEjGodkandAnvandare() {
    HospUppdatering hospUppdatering = new HospUppdatering();
    hospUppdatering.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringRepository.findSingle()).thenReturn(hospUppdatering);
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-05").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);
    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(false);
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // privatlakare1 får nu läkarbehörighet men har fått GODKAND_ANVANDARE false innan
    HospPerson hospPersonResponse1 = createGetHospPersonResponse();
    hospPersonResponse1.getTitleCodes().add("LK");
    hospPersonResponse1.getHsaTitles().add("Läkare");
    hospPersonResponse1.getSpecialityCodes().add("12");
    hospPersonResponse1.getSpecialityNames().add("Specialitet");
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse1);

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdatering.getSenasteHospUppdatering());
    verify(hospUppdateringRepository).save(hospUppdatering);

    // privatlakare1 should be updated with new hospinformation
    verify(privatlakareRepository).save(privatlakare1);
    // but should still be NOT_AUTHORIZED since GODKAND_ANVANDARE is false
    assertFalse(privatlakare1.isGodkandAnvandare());
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.NOT_AUTHORIZED,
        privatlakare1);
  }

  @Test(expected = HospUpdateFailedToContactHsaException.class)
  public void testUpdateHospInformationKanEjKontaktaHSA1()
      throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new WebServiceException("Could not send message"));

    try {
      hospUpdateService.updateHospInformation(privatlakare, true);
    } finally {
      verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
      verify(hospPersonService).getHospPerson(PERSON_ID);
      verifyNoMoreInteractions(hospPersonService);
    }
  }

  @Test(expected = HospUpdateFailedToContactHsaException.class)
  public void testUpdateHospInformationKanEjKontaktaHSA2()
      throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);

    when(hospPersonService.addToCertifier(eq(PERSON_ID), nullable(String.class)))
        .thenThrow(new WebServiceException("Could not send message"));

    try {
      hospUpdateService.updateHospInformation(privatlakare, true);
    } finally {
      verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
      verifyNoMoreInteractions(hospPersonService);
    }
  }

  @Test
  public void testUpdateHospInformationEjLakare() throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(createGetHospPersonResponse());

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakare, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(response, RegistrationStatus.NOT_AUTHORIZED);
  }

  @Test
  public void testUpdateHospInformationEjIHosp() throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    when(hospPersonService.addToCertifier(eq(PERSON_ID), nullable(String.class))).thenReturn(true);

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakare, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(response, RegistrationStatus.WAITING_FOR_HOSP);
  }

  @Test
  public void testUpdateHospInformationLakare() throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getTitleCodes().add("LK");
    hospPersonResponse.getHsaTitles().add("Läkare");
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse);

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakare, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(response, RegistrationStatus.AUTHORIZED);
  }

  @Test
  public void testUpdateHospInformationLakareEjGodkandAnvandare()
      throws HospUpdateFailedToContactHsaException {

    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(false);
    privatlakare.setPersonId(PERSON_ID);

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getTitleCodes().add("LK");
    hospPersonResponse.getHsaTitles().add("Läkare");
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse);

    RegistrationStatus response = hospUpdateService.updateHospInformation(privatlakare, true);

    verify(hospPersonService).addToCertifier(eq(PERSON_ID), nullable(String.class));
    assertEquals(response, RegistrationStatus.NOT_AUTHORIZED);
  }

  @Test
  public void testCheckForUpdatedHospInformationNotUpdated() {
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-01").atStartOfDay());

    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    verify(hospPersonService).getHospLastUpdate();
    verifyNoMoreInteractions(hospPersonService);
    verifyNoMoreInteractions(privatlakareRepository);
  }

  @Test
  public void testCheckForUpdatedHospInformationUpdated() {
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    HospPerson hospPersonResponse = createGetHospPersonResponse();
    hospPersonResponse.getTitleCodes().add("DT");
    hospPersonResponse.getHsaTitles().add("Dietist");
    hospPersonResponse.getSpecialityCodes().add("12");
    hospPersonResponse.getSpecialityNames().add("Specialitet");
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse);

    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verify(privatlakareRepository).save(privatlakare);

    assertEquals(LocalDate.parse("2015-09-05").atStartOfDay(),
        privatlakare.getSenasteHospUppdatering());
    assertEquals(1, privatlakare.getLegitimeradeYrkesgrupper().size());
    LegitimeradYrkesgrupp l = privatlakare.getLegitimeradeYrkesgrupper().iterator().next();
    assertEquals("DT", l.getKod());
    assertEquals("Dietist", l.getNamn());
    Specialitet s = privatlakare.getSpecialiteter().iterator().next();
    assertEquals("12", s.getKod());
    assertEquals("Specialitet", s.getNamn());
    assertEquals(PERSONAL_PRESCRIPTION_CODE, privatlakare.getForskrivarKod());
  }

  @Test
  public void testCheckForUpdatedHospInformationKanEjKontaktaHSA1() {
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenThrow(
        new WebServiceException("Could not send message"));
    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakare.getSenasteHospUppdatering());
  }

  @Test
  public void testCheckForUpdatedHospInformationKanEjKontaktaHSA2() {
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(
        new WebServiceException("Could not send message"));

    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakare.getSenasteHospUppdatering());
  }

  @Test
  public void testCheckForUpdatedHospInformationTillbakadragenLakarbehorighet() {
    // Haft läkarbehörighet innan.
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setForskrivarKod("7777777");
    Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<>();
    legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Läkare", "LK"));
    privatlakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);
    List<Specialitet> specialiteter = new ArrayList<>();
    specialiteter.add(new Specialitet(privatlakare, "Specialitet", "12"));
    privatlakare.setSpecialiteter(specialiteter);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    // Läkarbehörigheten är borttagen ur HSA
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verify(privatlakareRepository).save(privatlakare);

    assertEquals(LocalDate.parse("2015-09-05").atStartOfDay(),
        privatlakare.getSenasteHospUppdatering());
    assertEquals(0, privatlakare.getLegitimeradeYrkesgrupper().size());
    assertEquals(0, privatlakare.getSpecialiteter().size());
    assertEquals(null, privatlakare.getForskrivarKod());
  }

  @Test
  public void testCheckForUpdatedHospInformationTillbakadragenLakarbehorighetKanEjKontaktaHSA() {
    // Haft läkarbehörighet innan.
    Privatlakare privatlakare = new Privatlakare();
    privatlakare.setGodkandAnvandare(true);
    privatlakare.setPersonId(PERSON_ID);
    privatlakare.setForskrivarKod("7777777");
    Set<LegitimeradYrkesgrupp> legitimeradYrkesgrupper = new HashSet<>();
    legitimeradYrkesgrupper.add(new LegitimeradYrkesgrupp(privatlakare, "Läkare", "LK"));
    privatlakare.setLegitimeradeYrkesgrupper(legitimeradYrkesgrupper);
    List<Specialitet> specialiteter = new ArrayList<>();
    specialiteter.add(new Specialitet(privatlakare, "Specialitet", "12"));
    privatlakare.setSpecialiteter(specialiteter);
    privatlakare.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());

    when(hospPersonService.getHospLastUpdate()).thenReturn(
        LocalDate.parse("2015-09-05").atStartOfDay());

    // Läkarbehörigheten är borttagen ur HSA
    when(hospPersonService.getHospPerson(PERSON_ID)).thenThrow(new WebServiceException());

    hospUpdateService.checkForUpdatedHospInformation(privatlakare);

    // Om det ej gick att kontakta HSA ska datumet för lasthospupdate inte ändras.
    verify(hospPersonService).getHospLastUpdate();
    verify(hospPersonService).getHospPerson(PERSON_ID);
    verifyNoMoreInteractions(hospPersonService);
    verifyNoMoreInteractions(hospUppdateringRepository);
    verifyNoMoreInteractions(privatlakareRepository);

    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(),
        privatlakare.getSenasteHospUppdatering());
  }

  @Test
  public void testAwaitingHospNotificationMailSentCorrectly() {
    ReflectionTestUtils.setField(hospUpdateService, "lastUpdate",
        LocalDateTime.now().minusMinutes(15000));
    HospUppdatering hospUppdatering = new HospUppdatering();
    hospUppdatering.setSenasteHospUppdatering(LocalDate.now().minusDays(15).atStartOfDay());
    when(hospUppdateringRepository.findSingle()).thenReturn(hospUppdatering);

    LocalDateTime hospLastUpdate = LocalDate.now().atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(true);
    privatlakare1.setRegistreringsdatum(LocalDate.now().minusDays(10).atStartOfDay());
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdatering.getSenasteHospUppdatering());
    verify(hospUppdateringRepository).save(hospUppdatering);

    // Since registreringsdatum is 10 or more days before the last hospUpdate, a mail should be sent
    verify(mailService).sendRegistrationStatusEmail(RegistrationStatus.WAITING_FOR_HOSP,
        privatlakare1);
  }

  @Test
  public void testAwaitingHospNotificationMailNotSentBeforeAllottedTime() {
    HospUppdatering hospUppdatering = new HospUppdatering();
    hospUppdatering.setSenasteHospUppdatering(LocalDate.parse("2015-09-01").atStartOfDay());
    when(hospUppdateringRepository.findSingle()).thenReturn(hospUppdatering);

    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(true);
    // Set this within ten days of last hosp update
    privatlakare1.setRegistreringsdatum(LocalDate.parse("2015-09-10").atStartOfDay());
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    hospUpdateService.scheduledUpdateHospInformation();

    // sensateHospUppdatering in DB should be set to hospLastUpdate from HSA
    assertEquals(hospLastUpdate, hospUppdatering.getSenasteHospUppdatering());
    verify(hospUppdateringRepository).save(hospUppdatering);

    // Since registreringsdatum is less than 10 days before the last hospUpdate, a mail should not be sent
    verify(mailService, times(0)).sendRegistrationStatusEmail(RegistrationStatus.WAITING_FOR_HOSP,
        privatlakare1);
  }

  @Test
  public void testRegistrationRemovedAfterCorrectTimePeriod() {
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(true);
    privatlakare1.setRegistreringsdatum(LocalDate.now().minusDays(30).atStartOfDay());
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);
    when(hospPersonService.removeFromCertifier(eq(PERSON_ID), nullable(String.class),
        anyString())).thenReturn(true);

    hospUpdateService.scheduledUpdateHospInformation();

    verify(mailService).sendRegistrationRemovedEmail(privatlakare1);
    verify(monitoringLogService).logRegistrationRemoved(privatlakare1.getPersonId(),
        privatlakare1.getHsaId());
    verify(privatlakareRepository).delete(privatlakare1);
    verify(hospPersonService).removeFromCertifier(eq(PERSON_ID), nullable(String.class),
        anyString());
  }

  @Test
  public void testRegistrationNotRemovedWhileInAllowedTimeFrame() {
    LocalDateTime hospLastUpdate = LocalDate.parse("2015-09-15").atStartOfDay();
    ReflectionTestUtils.setField(hospUpdateService, "lastUpdate",
        LocalDate.parse("2015-09-15").atStartOfDay());
    when(hospPersonService.getHospLastUpdate()).thenReturn(hospLastUpdate);

    Privatlakare privatlakare1 = new Privatlakare();
    privatlakare1.setPersonId(PERSON_ID);
    privatlakare1.setGodkandAnvandare(true);
    privatlakare1.setRegistreringsdatum(LocalDate.now().minusDays(29).atStartOfDay());
    ArrayList<Privatlakare> list = new ArrayList<>();
    list.add(privatlakare1);
    when(privatlakareRepository.findNeverHadLakarBehorighet()).thenReturn(list);

    // No hosp-data available for user
    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    hospUpdateService.scheduledUpdateHospInformation();

    verify(mailService, times(0)).sendRegistrationRemovedEmail(privatlakare1);
    verify(privatlakareRepository, times(0)).delete(privatlakare1);
    verify(hospPersonService, times(0)).removeFromCertifier(anyString(), anyString(), anyString());
  }

  private HospPerson createGetHospPersonResponse() {
    HospPerson hospPerson = new HospPerson();

    hospPerson.setSpecialityCodes(new ArrayList());
    hospPerson.setSpecialityNames(new ArrayList());
    hospPerson.setTitleCodes(new ArrayList());
    hospPerson.setHsaTitles(new ArrayList<>());
    hospPerson.setPersonalPrescriptionCode(PERSONAL_PRESCRIPTION_CODE);

    return hospPerson;
  }
}
