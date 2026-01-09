package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Restriction;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.HashUtility;
import se.inera.intyg.privatepractitionerservice.infrastructure.mail.MailService;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.PrivatlakareEntityConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.EpostEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.RestriktionEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PrivatePractitionerRepository {

  private final PrivatlakareEntityRepository privatlakareEntityRepository;
  private final PrivatlakareIdEntityRepository privatlakareIdEntityRepository;
  private final PrivatlakareEntityConverter privatlakareEntityConverter;
  private final EpostEntityRepository epostEntityRepository;
  private final HashUtility hashUtility;
  private final MailService mailService;

  @Value("${mail.admin.notification.interval}")
  private int hsaIdNotificationInterval;

  public Optional<PrivatePractitioner> findByPersonId(String personId) {
    return privatlakareEntityRepository.findByPersonId(personId)
        .map(privatlakareEntityConverter::convert);
  }

  public Optional<PrivatePractitioner> findByHsaId(String hsaId) {
    return privatlakareEntityRepository.findByHsaId(hsaId)
        .map(privatlakareEntityConverter::convert);
  }

  public List<PrivatePractitioner> findAll() {
    return privatlakareEntityRepository.findAll().stream()
        .map(privatlakareEntityConverter::convert)
        .toList();
  }

  public boolean isExists(String personId) {
    return privatlakareEntityRepository.findByPersonId(personId).isPresent();
  }

  public PrivatePractitioner save(PrivatePractitioner privatePractitioner) {
    return privatlakareEntityRepository.findByPersonId(privatePractitioner.getPersonId())
        .map(existingEntity -> updateExisting(existingEntity, privatePractitioner))
        .orElseGet(() -> createNew(privatePractitioner));
  }

  /**
   * Resets (deletes) all private practitioner entities and resets the hsaidgenerationcounter. Used
   * in testability scenarios.
   *
   */
  public void clear() {
    privatlakareEntityRepository.deleteAll();
    privatlakareIdEntityRepository.deleteAll();
    privatlakareIdEntityRepository.resetIdSequence();
  }

  private PrivatePractitioner updateExisting(PrivatlakareEntity existingEntity,
      PrivatePractitioner privatePractitioner) {
    setDefaultValues(privatePractitioner, existingEntity);

    if (existingEntity.getEnhetStartdatum() == null) {
      existingEntity.setEnhetStartdatum(privatePractitioner.getStartDate());
    }

    if (existingEntity.getVardgivareStartdatum() == null) {
      existingEntity.setVardgivareStartdatum(privatePractitioner.getStartDate());
    }

    final var savedEntity = privatlakareEntityRepository.save(existingEntity);
    return privatlakareEntityConverter.convert(savedEntity);
  }

  private PrivatePractitioner createNew(PrivatePractitioner privatePractitioner) {
    final var id = privatlakareIdEntityRepository.save(new PrivatlakareIdEntity());
    final var hsaId = generateHsaId(id);

    final var newEntity = new PrivatlakareEntity();
    newEntity.setPersonId(privatePractitioner.getPersonId());
    newEntity.setEnhetsId(hsaId);
    newEntity.setHsaId(hsaId);
    newEntity.setVardgivareId(hsaId);
    newEntity.setGodkandAnvandare(true);
    newEntity.setRegistreringsdatum(privatePractitioner.getRegistrationDate());

    setDefaultValues(privatePractitioner, newEntity);

    final var savedEntity = privatlakareEntityRepository.save(newEntity);

    sendAdminNotification();

    return privatlakareEntityConverter.convert(savedEntity);
  }

  private void sendAdminNotification() {
    final var latestGeneratedHsaId = privatlakareIdEntityRepository.findLatestGeneratedHsaId();
    if (latestGeneratedHsaId != 0
        && latestGeneratedHsaId % hsaIdNotificationInterval
        == 0) {
      mailService.sendHsaGenerationEmail(latestGeneratedHsaId);
    }
  }

  private void setDefaultValues(PrivatePractitioner privatePractitioner,
      PrivatlakareEntity entity) {
    entity.setFullstandigtNamn(privatePractitioner.getName());
    entity.setEnhetsNamn(privatePractitioner.getCareUnitName());
    entity.setVardgivareNamn(privatePractitioner.getCareProviderName());
    entity.setArbetsplatsKod(privatePractitioner.getWorkplaceCode());

    entity.setPostadress(privatePractitioner.getAddress());
    entity.setPostnummer(privatePractitioner.getZipCode());
    entity.setPostort(privatePractitioner.getCity());
    entity.setTelefonnummer(privatePractitioner.getPhoneNumber());
    entity.setEpost(privatePractitioner.getEmail());

    entity.setKommun(privatePractitioner.getMunicipality());
    entity.setLan(privatePractitioner.getCounty());

    entity.setAgarform(privatePractitioner.getOwnershipType());

    entity.updateBefattningar(privatePractitioner.getPosition());
    entity.updateVardformer(privatePractitioner.getTypeOfCare());
    entity.updateVerksamhetstyper(privatePractitioner.getHealthcareServiceType());

    setHospInfo(privatePractitioner, entity);

  }

  private void setHospInfo(PrivatePractitioner privatePractitioner, PrivatlakareEntity entity) {
    entity.setSenasteHospUppdatering(privatePractitioner.getHospUpdated());
    entity.setForskrivarKod(privatePractitioner.getPersonalPrescriptionCode());

    entity.setSpecialiteter(getSpecialiteter(privatePractitioner.getSpecialties()));
    entity.setRestriktioner(getRestrictions(privatePractitioner.getRestrictions()));
    entity.setLegitimeradeYrkesgrupper(
        getLegitimeradeYrkesgrupper(privatePractitioner.getLicensedHealthcareProfessions()));
  }

  private String generateHsaId(PrivatlakareIdEntity privatlakareIdEntity) {
    return "SE165565594230-WEBCERT" + StringUtils.leftPad(
        Integer.toString(privatlakareIdEntity.getId()),
        5,
        '0'
    );
  }

  private List<SpecialitetEntity> getSpecialiteter(List<Speciality> specialities) {
    return specialities.stream()
        .map(speciality -> new SpecialitetEntity(
            speciality.name(),
            speciality.code()
        ))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<RestriktionEntity> getRestrictions(List<Restriction> restrictions) {
    return restrictions.stream()
        .map(
            restriction -> new RestriktionEntity(restriction.code(),
                restriction.name()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<LegitimeradYrkesgruppEntity> getLegitimeradeYrkesgrupper(
      List<LicensedHealtcareProfession> licensedHealtcareProfessions) {
    return licensedHealtcareProfessions.stream()
        .map(licensedHealtcareProfession -> new LegitimeradYrkesgruppEntity(
            licensedHealtcareProfession.name(),
            licensedHealtcareProfession.code()
        ))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<PrivatePractitioner> findPrivatePractitionersNeedingHospUpdate() {
    return privatlakareEntityRepository.findNeverHadLakarBehorighet().stream()
        .map(privatlakareEntityConverter::convert)
        .toList();
  }

  public void remove(PrivatePractitioner privatePractitioner) {
    privatlakareEntityRepository.findByPersonId(privatePractitioner.getPersonId())
        .ifPresentOrElse(
            privatlakareEntityRepository::delete,
            () -> log.warn("Tried to remove non-existing private practitioner with personId '{}'",
                hashUtility.hash(privatePractitioner.getPersonId()))
        );
  }

  public void addEmailNotification(String personId, LocalDateTime sent) {
    privatlakareEntityRepository.findByPersonId(personId)
        .ifPresentOrElse(
            entity -> epostEntityRepository.save(
                EpostEntity.builder()
                    .privatlakareId(entity.getPrivatlakareId())
                    .skickadDatum(sent)
                    .build()
            ),
            () -> log.warn(
                "Tried to add email notification to non-existing private practitioner with personId '{}'",
                hashUtility.hash(personId)
            )
        );
  }
}
