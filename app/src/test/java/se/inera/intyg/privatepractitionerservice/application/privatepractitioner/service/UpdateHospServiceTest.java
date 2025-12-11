package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.DR_KRANSTEGE_HOSP_PERSON;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeBuilder;
import static se.inera.intyg.privatepractitionerservice.testdata.TestDataModel.kranstegeHospPersonBuilder;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.HospRepository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@ExtendWith(MockitoExtension.class)
class UpdateHospServiceTest {

  @Mock
  private HospRepository hospRepository;
  @Mock
  private PrivatePractitionerRepository privatePractitionerRepository;
  @Mock
  private NotifyPrivatePractitionerRegistration notifyPrivatePractitionerRegistration;
  @Mock
  private HandleWaitingForHospService handleWaitingForHospService;
  @Mock
  private RemovePrivatePractitionerService removePrivatePractitionerService;

  @InjectMocks
  private UpdateHospService service;

  @Test
  void shouldNotPerformUpdateWhenHospSaysNoNeed() {
    when(hospRepository.needUpdateFromHosp()).thenReturn(false);

    service.update();

    verify(hospRepository, never()).hospUpdated();
    verifyNoInteractions(privatePractitionerRepository, notifyPrivatePractitionerRegistration,
        handleWaitingForHospService, removePrivatePractitionerService);
  }

  @Test
  void shouldMarkHospUpdatedWhenNoPractitionersToProcess() {
    when(hospRepository.needUpdateFromHosp()).thenReturn(true);
    when(privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate()).thenReturn(
        emptyList());

    service.update();

    verify(hospRepository).hospUpdated();

    verify(privatePractitionerRepository, never()).save(any());
    verify(hospRepository, never()).addToCertifier(any());
    verifyNoInteractions(notifyPrivatePractitionerRegistration, handleWaitingForHospService,
        removePrivatePractitionerService);
  }

  @Test
  void shouldProcessAuthorizedPractitioner() {
    when(hospRepository.needUpdateFromHosp()).thenReturn(true);

    final var kranstege = kranstegeBuilder().build();
    when(privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate()).thenReturn(
        List.of(kranstege));

    when(hospRepository.findByPersonId(kranstege.getPersonId())).thenReturn(
        DR_KRANSTEGE_HOSP_PERSON);

    service.update();

    verify(privatePractitionerRepository).save(kranstege);
    verify(hospRepository).addToCertifier(kranstege);

    verify(notifyPrivatePractitionerRegistration).notify(kranstege);
    verify(removePrivatePractitionerService, never()).remove(any());
    verify(handleWaitingForHospService, never()).handle(any());

    verify(hospRepository).hospUpdated();
  }

  @Test
  void shouldProcessNotAuthorizedPractitioner() {
    when(hospRepository.needUpdateFromHosp()).thenReturn(true);

    final var kranstege = kranstegeBuilder().build();
    when(privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate()).thenReturn(
        List.of(kranstege));

    when(hospRepository.findByPersonId(kranstege.getPersonId())).thenReturn(
        kranstegeHospPersonBuilder()
            .licensedHealthcareProfessions(
                List.of(new LicensedHealtcareProfession("NA", "Naprapat")))
            .build());

    service.update();

    verify(privatePractitionerRepository).save(kranstege);
    verify(hospRepository).addToCertifier(kranstege);

    verify(removePrivatePractitionerService).remove(kranstege);
    verify(notifyPrivatePractitionerRegistration, never()).notify(any());
    verify(handleWaitingForHospService, never()).handle(any());

    verify(hospRepository).hospUpdated();
  }

  @Test
  void shouldProcessWaitingPractitioner() {
    when(hospRepository.needUpdateFromHosp()).thenReturn(true);

    final var kranstege = kranstegeBuilder().build();
    when(privatePractitionerRepository.findPrivatePractitionersNeedingHospUpdate()).thenReturn(
        List.of(kranstege));

    when(hospRepository.findByPersonId(kranstege.getPersonId())).thenReturn(
        kranstegeHospPersonBuilder()
            .licensedHealthcareProfessions(List.of())
            .build());

    service.update();

    verify(privatePractitionerRepository).save(kranstege);
    verify(hospRepository).addToCertifier(kranstege);

    verify(handleWaitingForHospService).handle(kranstege);
    verify(notifyPrivatePractitionerRegistration, never()).notify(any());
    verify(removePrivatePractitionerService, never()).remove(any());

    verify(hospRepository).hospUpdated();
  }
}
