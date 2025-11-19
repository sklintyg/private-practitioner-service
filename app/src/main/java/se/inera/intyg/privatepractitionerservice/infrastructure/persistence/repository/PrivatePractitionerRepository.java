package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.PrivatlakareEntityConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;

@Repository
@RequiredArgsConstructor
public class PrivatePractitionerRepository {

  private final PrivatlakareEntityRepository privatlakareEntityRepository;
  private final PrivatlakareIdEntityRepository privatlakareIdEntityRepository;
  private final PrivatlakareEntityConverter privatlakareEntityConverter;

  public Optional<PrivatePractitioner> findByPersonId(String personId) {
    return Optional.ofNullable(
            privatlakareEntityRepository.findByPersonId(personId)
        )
        .map(privatlakareEntityConverter::convert);
  }

  public Optional<PrivatePractitioner> findByHsaId(String hsaId) {
    return Optional.ofNullable(
            privatlakareEntityRepository.findByHsaId(hsaId)
        )
        .map(privatlakareEntityConverter::convert);
  }

  public List<PrivatePractitioner> findAll() {
    return privatlakareEntityRepository.findAll().stream()
        .map(privatlakareEntityConverter::convert)
        .toList();
  }

  public boolean isExists(String personId) {
    return privatlakareEntityRepository.findByPersonId(personId) != null;
  }

  public PrivatePractitioner save(PrivatePractitioner privatePractitioner) {
    if (privatlakareEntityRepository.findByPersonId(privatePractitioner.getPersonId()) != null) {
      return updateExisting(privatePractitioner);
    }
    return createNew(privatePractitioner);
  }

  public void reset(List<String> personIds) {
    personIds
        .forEach(personId -> {
          final var existingEntity = privatlakareEntityRepository.findByPersonId(personId);
          if (existingEntity != null) {
            privatlakareEntityRepository.delete(existingEntity);
          }
        });
  }

  private PrivatePractitioner updateExisting(PrivatePractitioner privatePractitioner) {
    final var existingEntity = privatlakareEntityRepository.findByPersonId(
        privatePractitioner.getPersonId());

    existingEntity.setFullstandigtNamn(privatePractitioner.getName());
    existingEntity.setEnhetsNamn(privatePractitioner.getCareProviderName());
    existingEntity.setVardgivareNamn(privatePractitioner.getCareProviderName());
    existingEntity.setArbetsplatsKod(privatePractitioner.getWorkplaceCode());
    existingEntity.setForskrivarKod(privatePractitioner.getPersonalPrescriptionCode());

    existingEntity.setPostadress(privatePractitioner.getAddress());
    existingEntity.setPostnummer(privatePractitioner.getZipCode());
    existingEntity.setPostort(privatePractitioner.getCity());
    existingEntity.setTelefonnummer(privatePractitioner.getPhoneNumber());
    existingEntity.setEpost(privatePractitioner.getEmail());

    existingEntity.setKommun(privatePractitioner.getMunicipality());
    existingEntity.setLan(privatePractitioner.getCounty());

    existingEntity.setAgarform(privatePractitioner.getOwnershipType());

    existingEntity.updateBefattningar(privatePractitioner.getPosition());
    existingEntity.updateVardformer(privatePractitioner.getTypeOfCare());
    existingEntity.updateVerksamhetstyper(privatePractitioner.getHealthcareServiceType());

    existingEntity.setSpecialiteter(
        getSpecialiteter(privatePractitioner.getSpecialties())
    );

    existingEntity.setLegitimeradeYrkesgrupper(
        getLegitimeradeYrkesgrupper(privatePractitioner.getLicensedHealthcareProfessions())
    );

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

    newEntity.setFullstandigtNamn(privatePractitioner.getName());
    newEntity.setEnhetsNamn(privatePractitioner.getCareProviderName());
    newEntity.setVardgivareNamn(privatePractitioner.getCareProviderName());
    newEntity.setArbetsplatsKod(privatePractitioner.getWorkplaceCode());
    newEntity.setForskrivarKod(privatePractitioner.getPersonalPrescriptionCode());

    newEntity.setPostadress(privatePractitioner.getAddress());
    newEntity.setPostnummer(privatePractitioner.getZipCode());
    newEntity.setPostort(privatePractitioner.getCity());
    newEntity.setTelefonnummer(privatePractitioner.getPhoneNumber());
    newEntity.setEpost(privatePractitioner.getEmail());

    newEntity.setKommun(privatePractitioner.getMunicipality());
    newEntity.setLan(privatePractitioner.getCounty());

    newEntity.setAgarform(privatePractitioner.getOwnershipType());
    newEntity.setGodkandAnvandare(true);
    newEntity.setRegistreringsdatum(privatePractitioner.getRegistrationDate());

    newEntity.updateBefattningar(privatePractitioner.getPosition());
    newEntity.updateVardformer(privatePractitioner.getTypeOfCare());
    newEntity.updateVerksamhetstyper(privatePractitioner.getHealthcareServiceType());

    newEntity.setSpecialiteter(
        getSpecialiteter(privatePractitioner.getSpecialties())
    );

    newEntity.setLegitimeradeYrkesgrupper(
        getLegitimeradeYrkesgrupper(privatePractitioner.getLicensedHealthcareProfessions())
    );
    final var savedEntity = privatlakareEntityRepository.save(newEntity);

// TODO: Add logic to send email notification about HSA ID generation status
//    if (privatlakareIdEntityRepository.findLatestGeneratedHsaId() != 0
//        && privatlakareIdEntityRepository.findLatestGeneratedHsaId() % hsaIdNotificationInterval
//        == 0) {
//      mailService.sendHsaGenerationStatusEmail();
//    }

    return privatlakareEntityConverter.convert(savedEntity);
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

  private List<LegitimeradYrkesgruppEntity> getLegitimeradeYrkesgrupper(
      List<LicensedHealtcareProfession> licensedHealtcareProfessions) {
    return licensedHealtcareProfessions.stream()
        .map(licensedHealtcareProfession -> new LegitimeradYrkesgruppEntity(
            licensedHealtcareProfession.name(),
            licensedHealtcareProfession.code()
        ))
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
