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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.xml.ws.WebServiceException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.springframework.core.io.ClassPathResource;
import se.inera.intyg.privatlakarportal.auth.PrivatlakarUser;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.common.integration.json.CustomObjectMapper;
import se.inera.intyg.privatlakarportal.common.model.Registration;
import se.inera.intyg.privatlakarportal.common.model.RegistrationStatus;
import se.inera.intyg.privatlakarportal.common.service.DateHelperService;
import se.inera.intyg.privatlakarportal.common.service.MailService;
import se.inera.intyg.privatlakarportal.common.service.stub.MailStubStore;
import se.inera.intyg.privatlakarportal.hsa.model.HospPerson;
import se.inera.intyg.privatlakarportal.hsa.services.HospPersonService;
import se.inera.intyg.privatlakarportal.hsa.services.HospUpdateService;
import se.inera.intyg.privatlakarportal.hsa.services.exception.HospUpdateFailedToContactHsaException;
import se.inera.intyg.privatlakarportal.persistence.model.Befattning;
import se.inera.intyg.privatlakarportal.persistence.model.LegitimeradYrkesgrupp;
import se.inera.intyg.privatlakarportal.persistence.model.Medgivande;
import se.inera.intyg.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.model.PrivatlakareId;
import se.inera.intyg.privatlakarportal.persistence.model.Specialitet;
import se.inera.intyg.privatlakarportal.persistence.model.Vardform;
import se.inera.intyg.privatlakarportal.persistence.model.Verksamhetstyp;
import se.inera.intyg.privatlakarportal.persistence.repository.MedgivandeTextRepository;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareIdRepository;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.model.HospInformation;
import se.inera.intyg.privatlakarportal.service.model.RegistrationWithHospInformation;
import se.inera.intyg.privatlakarportal.service.model.SaveRegistrationResponseStatus;
import se.inera.intyg.privatlakarportal.service.monitoring.MonitoringLogService;
import se.inera.intyg.privatlakarportal.web.integration.test.dto.PrivatlakareDto;

@RunWith(MockitoJUnitRunner.class)
public class RegisterServiceImplTest {

  private static final String PERSON_ID = "191212121212";

  @Mock
  private PrivatlakareRepository privatlakareRepository;

  @Mock
  private PrivatlakareIdRepository privatlakareidRepository;

  @Mock
  private MedgivandeTextRepository medgivandeTextRepository;

  @Mock
  private HospPersonService hospPersonService;

  @Mock
  private HospUpdateService hospUpdateService;

  @Mock
  private UserService userService;

  @Mock
  private MailService mailService;

  @Mock
  private DateHelperService dateHelperService;

  @Mock
  private MonitoringLogService monitoringLogService;

  @Mock
  private TermsService termsService;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @InjectMocks
  private RegisterService registerService = new RegisterServiceImpl();

  private final MailStubStore mailStore = new MailStubStore();

  private Privatlakare readPrivatlakare(String path) throws IOException {
    Privatlakare verifyPrivatlakare = new CustomObjectMapper().readValue(new ClassPathResource(
        path).getFile(), Privatlakare.class);
    for (Befattning befattning : verifyPrivatlakare.getBefattningar()) {
      befattning.setPrivatlakare(verifyPrivatlakare);
    }
    for (Verksamhetstyp verksamhetstyp : verifyPrivatlakare.getVerksamhetstyper()) {
      verksamhetstyp.setPrivatlakare(verifyPrivatlakare);
    }
    for (Vardform vardform : verifyPrivatlakare.getVardformer()) {
      vardform.setPrivatlakare(verifyPrivatlakare);
    }
    for (Medgivande medgivande : verifyPrivatlakare.getMedgivande()) {
      medgivande.setPrivatlakare(verifyPrivatlakare);
    }
    return verifyPrivatlakare;
  }

  private Registration createValidRegistration() {
    Registration registration = new Registration();

    registration.setVerksamhetensNamn("Test verksamhet");
    registration.setArbetsplatskod("0000000");
    registration.setAgarForm("Privat");
    registration.setAdress("Test adress");
    registration.setPostnummer("12345");
    registration.setPostort("Test postort");
    registration.setTelefonnummer("123456789");
    registration.setEpost("test@example.com");
    registration.setBefattning("201010");
    registration.setVerksamhetstyp("10");
    registration.setVardform("01");
    registration.setLan("Test län");
    registration.setKommun("Test kommun");

    return registration;
  }

  @Before
  public void setup() {
    PrivatlakarUser privatlakarUser = new PrivatlakarUser(PERSON_ID, "Test User", "authScheme");
    privatlakarUser.updateNameFromPuService("Test User");
    when(userService.getUser()).thenReturn(privatlakarUser);

    MedgivandeText medgivandeText = new MedgivandeText();
    medgivandeText.setDatum(LocalDate.parse("2015-08-01").atStartOfDay());
    medgivandeText.setMedgivandeText("Medgivandetext");
    medgivandeText.setVersion(1L);
    when(medgivandeTextRepository.findById(1L)).thenReturn(Optional.of(medgivandeText));

    when(dateHelperService.now()).thenReturn(LocalDate.parse("2015-09-09").atStartOfDay());

    registerService.injectHsaInterval(50);
  }

  @Test
  public void testHsaMailSent() throws HospUpdateFailedToContactHsaException {

    // Notify admin every 50 registrations
    final int sendHsaMailInterval = 50;

    Mockito.doAnswer(invocation -> {
      mailStore.addMail("ADMIN-EMAIL", "TEST");
      return null;
    }).when(mailService).sendHsaGenerationStatusEmail();

    // Create enough registrations to reach the threshold
    for (int i = 1; i <= sendHsaMailInterval; i++) {
      PrivatlakareId privatlakareId = new PrivatlakareId();
      privatlakareId.setId(i);
      when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);
      when(privatlakareidRepository.findLatestGeneratedHsaId()).thenReturn(i);

      when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(
          RegistrationStatus.AUTHORIZED);
      Registration registration = createValidRegistration();
      registerService.createRegistration(registration, 1L);
    }
    assertTrue(mailStore.getMails().containsKey("ADMIN-EMAIL"));
  }

  @Test
  public void testHsaMailThreshold() throws HospUpdateFailedToContactHsaException {
    // Notify admin every 50 registrations
    final int sendHsaMailInterval = 49;

    // Create one less registration than the threshold
    for (int i = 1; i <= sendHsaMailInterval; i++) {
      PrivatlakareId privatlakareId = new PrivatlakareId();
      privatlakareId.setId(i);
      when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);
      when(privatlakareidRepository.findLatestGeneratedHsaId()).thenReturn(i);

      when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(
          RegistrationStatus.AUTHORIZED);
      Registration registration = createValidRegistration();
      registerService.createRegistration(registration, 1L);
    }
    assertFalse(mailStore.getMails().containsKey("ADMIN-EMAIL"));
  }

  @Test
  public void testInvalidCreateRegistration() {

    Registration registration = new Registration();
    PrivatlakarportalServiceException exception = assertThrows(
        PrivatlakarportalServiceException.class, () -> registerService
            .createRegistration(registration, 1L));
    assertEquals(PrivatlakarportalErrorCodeEnum.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  public void testcreatePrivatlakareAlreadyExists() {

    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

    Registration registration = createValidRegistration();
    PrivatlakarportalServiceException exception = assertThrows(
        PrivatlakarportalServiceException.class, () -> registerService
            .createRegistration(registration, 1L));
    assertEquals(PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS, exception.getErrorCode());
  }

  @Test
  public void testCreateRegistrationLakare()
      throws IOException, HospUpdateFailedToContactHsaException {

    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);
    when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

    when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(
        RegistrationStatus.AUTHORIZED);

    Registration registration = createValidRegistration();
    RegistrationStatus response = registerService.createRegistration(registration, 1L);

    ArgumentCaptor<Privatlakare> savedPrivatlakare = ArgumentCaptor.forClass(Privatlakare.class);
    verify(privatlakareRepository).save(savedPrivatlakare.capture());
    assertEquals(response, RegistrationStatus.AUTHORIZED);
    assertEquals(1, savedPrivatlakare.getValue().getMedgivande().size());

    Privatlakare verifyPrivatlakare = readPrivatlakare("RegisterServiceImplTest/test.json");
    assertThat(savedPrivatlakare.getValue())
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(verifyPrivatlakare);
  }

  @Test
  public void testCreateRegistrationLakareUtanMedgivande() {
    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);
    Registration registration = createValidRegistration();

    PrivatlakarportalServiceException exception = assertThrows(
        PrivatlakarportalServiceException.class, () -> registerService
            .createRegistration(registration, null));
    assertEquals(PrivatlakarportalErrorCodeEnum.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  public void testCreateRegistrationLakareFelMedgivandeVersion() {
    when(medgivandeTextRepository.findById(2L)).thenReturn(Optional.empty());

    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);

    Registration registration = createValidRegistration();
    PrivatlakarportalServiceException exception = assertThrows(
        PrivatlakarportalServiceException.class, () -> registerService
            .createRegistration(registration, 2L));
    assertEquals(PrivatlakarportalErrorCodeEnum.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  public void testCreateRegistrationEjLakare() throws HospUpdateFailedToContactHsaException {

    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);
    when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

    when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(
        RegistrationStatus.NOT_AUTHORIZED);

    Registration registration = createValidRegistration();
    RegistrationStatus response = registerService.createRegistration(registration, 1L);

    verify(privatlakareRepository).save(any(Privatlakare.class));
    assertEquals(response, RegistrationStatus.NOT_AUTHORIZED);
  }

  @Test
  public void testCreateRegistrationEjIHosp() throws HospUpdateFailedToContactHsaException {

    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);
    when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

    when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenReturn(
        RegistrationStatus.WAITING_FOR_HOSP);

    Registration registration = createValidRegistration();
    RegistrationStatus response = registerService.createRegistration(registration, 1L);

    verify(privatlakareRepository).save(any(Privatlakare.class));
    assertEquals(response, RegistrationStatus.WAITING_FOR_HOSP);
  }

  @Test
  public void testCreateRegistrationHospFail() throws HospUpdateFailedToContactHsaException {

    PrivatlakareId privatlakareId = new PrivatlakareId();
    privatlakareId.setId(1);
    when(privatlakareidRepository.save(any(PrivatlakareId.class))).thenReturn(privatlakareId);

    when(hospUpdateService.updateHospInformation(any(Privatlakare.class), eq(true))).thenThrow(
        new HospUpdateFailedToContactHsaException(new WebServiceException("Fail")));

    Registration registration = createValidRegistration();
    RegistrationStatus response = registerService.createRegistration(registration, 1L);

    verify(privatlakareRepository).save(any(Privatlakare.class));
    assertEquals(response, RegistrationStatus.WAITING_FOR_HOSP);
  }

  @Test
  public void testCreateRegistrationEjIPUService() {

    when(userService.getUser()).thenReturn(
        new PrivatlakarUser(PERSON_ID, "Test User", "authScheme"));

    Registration registration = createValidRegistration();
    PrivatlakarportalServiceException exception = assertThrows(
        PrivatlakarportalServiceException.class, () -> registerService
            .createRegistration(registration, 1L));
    assertEquals(PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, exception.getErrorCode());

    verify(privatlakareRepository).findByPersonId(
        new PrivatlakarUser(PERSON_ID, "Test User", "authScheme").getPersonalIdentityNumber());
    verifyNoMoreInteractions(privatlakareRepository);
    verifyNoMoreInteractions(hospUpdateService);
  }

  @Test
  public void testSavePrivatlakare() {

    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

    Registration registration = createValidRegistration();
    SaveRegistrationResponseStatus response = registerService.saveRegistration(registration);

    verify(privatlakareRepository).save(any(Privatlakare.class));
    assertEquals(response, SaveRegistrationResponseStatus.OK);
  }

  @Test
  public void testSavePrivatlakareNotFound() {

    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);

    Registration registration = createValidRegistration();
    PrivatlakarportalServiceException exception =
        assertThrows(PrivatlakarportalServiceException.class,
            () -> registerService.saveRegistration(registration));
    assertEquals(PrivatlakarportalErrorCodeEnum.NOT_FOUND, exception.getErrorCode());
  }

  @Test
  public void testSavePrivatlakareInvalid() {

    Registration registration = new Registration();
    PrivatlakarportalServiceException exception =
        assertThrows(PrivatlakarportalServiceException.class,
            () -> registerService.saveRegistration(registration));
    assertEquals(PrivatlakarportalErrorCodeEnum.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  public void testRemovePrivatlakare() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(new Privatlakare());

    Registration registration = createValidRegistration();

    registerService.saveRegistration(registration);

    assertTrue(registerService.removePrivatlakare(PERSON_ID));
  }

  @Test
  public void testRemoveNonExistingPrivatlakare() {
    assertFalse(registerService.removePrivatlakare("195206142597"));
  }

  @Test
  public void getHospInformation() {

    HospPerson hospPersonResponse = new HospPerson();
    hospPersonResponse.setPersonalIdentityNumber(PERSON_ID);
    hospPersonResponse.setPersonalPrescriptionCode("0000000");
    hospPersonResponse.getHsaTitles().add("Test title");
    hospPersonResponse.getSpecialityNames().add("Test speciality");

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(hospPersonResponse);

    HospInformation hospInformation = registerService.getHospInformation();

    assertEquals(hospInformation.getPersonalPrescriptionCode(), "0000000");
    assertEquals(hospInformation.getHsaTitles().size(), 1);
    assertEquals(hospInformation.getHsaTitles().get(0), "Test title");
    assertEquals(hospInformation.getSpecialityNames().size(), 1);
    assertEquals(hospInformation.getSpecialityNames().get(0), "Test speciality");
  }

  @Test
  public void getHospInformationNotInHosp() {

    when(hospPersonService.getHospPerson(PERSON_ID)).thenReturn(null);

    HospInformation hospInformation = registerService.getHospInformation();

    assertNull(hospInformation);
  }

  @Test
  public void getRegistration() throws IOException {
    Privatlakare testPrivatlakare = readPrivatlakare("RegisterServiceImplTest/test_lakare.json");
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(testPrivatlakare);
    when(termsService.getWebcertUserTermsApproved(any(String.class))).thenReturn(true);

    RegistrationWithHospInformation registration = registerService.getRegistration();

    // assert registration information
    assertEquals(testPrivatlakare.getPostadress(), registration.getRegistration().getAdress());
    assertEquals(testPrivatlakare.getAgarform(), registration.getRegistration().getAgarForm());
    assertEquals(testPrivatlakare.getArbetsplatsKod(),
        registration.getRegistration().getArbetsplatskod());
    assertEquals(testPrivatlakare.getBefattningar().iterator().next().getKod(),
        registration.getRegistration().getBefattning());
    assertEquals(testPrivatlakare.getEpost(), registration.getRegistration().getEpost());
    assertEquals(testPrivatlakare.getKommun(), registration.getRegistration().getKommun());
    assertEquals(testPrivatlakare.getLan(), registration.getRegistration().getLan());
    assertEquals(testPrivatlakare.getPostnummer(), registration.getRegistration().getPostnummer());
    assertEquals(testPrivatlakare.getPostort(), registration.getRegistration().getPostort());
    assertEquals(testPrivatlakare.getTelefonnummer(),
        registration.getRegistration().getTelefonnummer());
    assertEquals(testPrivatlakare.getVardformer().iterator().next().getKod(),
        registration.getRegistration().getVardform());
    assertEquals(testPrivatlakare.getEnhetsNamn(),
        registration.getRegistration().getVerksamhetensNamn());
    assertEquals(testPrivatlakare.getVerksamhetstyper().iterator().next().getKod(),
        registration.getRegistration().getVerksamhetstyp());
    // assert Hosp-information
    assertEquals(testPrivatlakare.getLegitimeradeYrkesgrupper().size(),
        registration.getHospInformation().getHsaTitles().size());
    for (LegitimeradYrkesgrupp legitimeradYrkesgrupp : testPrivatlakare.getLegitimeradeYrkesgrupper()) {
      assertTrue(registration.getHospInformation().getHsaTitles()
          .contains(legitimeradYrkesgrupp.getNamn()));
    }
    assertEquals(testPrivatlakare.getForskrivarKod(),
        registration.getHospInformation().getPersonalPrescriptionCode());
    assertEquals(testPrivatlakare.getSpecialiteter().size(),
        registration.getHospInformation().getSpecialityNames().size());
    for (Specialitet specialitet : testPrivatlakare.getSpecialiteter()) {
      assertTrue(
          registration.getHospInformation().getSpecialityNames().contains(specialitet.getNamn()));
    }
  }

  @Test
  public void getEmptyRegistration() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);

    RegistrationWithHospInformation registration = registerService.getRegistration();
    assertNull(registration.getHospInformation());
    assertNull(registration.getRegistration());
    assertFalse(registration.isWebcertUserTermsApproved());
  }

  @Test
  public void getPrivatlakareFound() {
    var privatlakare = new Privatlakare();
    privatlakare.setVardformer(Collections.emptySet());
    privatlakare.setSpecialiteter(Collections.emptyList());
    privatlakare.setLegitimeradeYrkesgrupper(Collections.emptySet());
    privatlakare.setMedgivande(Collections.emptySet());
    privatlakare.setBefattningar(Collections.emptySet());
    privatlakare.setVerksamhetstyper(Collections.emptySet());
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(privatlakare);

    PrivatlakareDto privatlakareDto = registerService.getPrivatlakare(PERSON_ID);
    assertNotNull(privatlakareDto);
  }

  @Test
  public void getPrivatlakareNotFound() {
    when(privatlakareRepository.findByPersonId(PERSON_ID)).thenReturn(null);

    PrivatlakareDto privatlakareDto = registerService.getPrivatlakare(PERSON_ID);
    assertNull(privatlakareDto);
  }

}
