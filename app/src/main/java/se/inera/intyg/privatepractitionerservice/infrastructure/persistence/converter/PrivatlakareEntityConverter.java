package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import java.util.List;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.LegitimeradYrkesgruppEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.SpecialitetEntity;

@Component
public class PrivatlakareEntityConverter {

  public PrivatePractitioner convert(PrivatlakareEntity privatlakareEntity) {
    if (privatlakareEntity == null) {
      return null;
    }

    return PrivatePractitioner.builder()
        .hsaId(privatlakareEntity.getHsaId())
        .personId(privatlakareEntity.getPersonId())
        .name(privatlakareEntity.getFullstandigtNamn())
        .careProviderName(privatlakareEntity.getVardgivareNamn())
        .careUnitName(privatlakareEntity.getEnhetsNamn())
        .position(getFirstBefattningKod(privatlakareEntity))
        .workplaceCode(privatlakareEntity.getArbetsplatsKod())
        .ownershipType(privatlakareEntity.getAgarform())
        .typeOfCare(getFirstVardformKod(privatlakareEntity))
        .healthcareServiceType(getFirstVerksamhetstypKod(privatlakareEntity))
        .phoneNumber(privatlakareEntity.getTelefonnummer())
        .email(privatlakareEntity.getEpost())
        .address(privatlakareEntity.getPostadress())
        .zipCode(privatlakareEntity.getPostnummer())
        .city(privatlakareEntity.getPostort())
        .municipality(privatlakareEntity.getKommun())
        .county(privatlakareEntity.getLan())
        .personalPrescriptionCode(privatlakareEntity.getForskrivarKod())
        .specialties(convertSpecialiteter(privatlakareEntity.getSpecialiteter()))
        .licensedHealthcareProfessions(
            convertLegitimeradeYrkesgrupper(privatlakareEntity.getLegitimeradeYrkesgrupper()))
        .startDate(privatlakareEntity.getVardgivareStartdatum())
        .endDate(privatlakareEntity.getVardgivareSlutdatum())
        .registrationDate(privatlakareEntity.getRegistreringsdatum())
        .hospUpdated(privatlakareEntity.getSenasteHospUppdatering())
        .build();
  }

  private String getFirstBefattningKod(PrivatlakareEntity entity) {
    if (entity == null || entity.getBefattningar() == null || entity.getBefattningar().isEmpty()) {
      return null;
    }

    final var position = entity.getBefattningar().getFirst();
    return position == null ? null : position.getKod();
  }

  private String getFirstVerksamhetstypKod(PrivatlakareEntity entity) {
    if (entity == null || entity.getVerksamhetstyper() == null || entity.getVerksamhetstyper()
        .isEmpty()) {
      return null;
    }

    final var healthcareServiceType = entity.getVerksamhetstyper().getFirst();
    return healthcareServiceType == null ? null : healthcareServiceType.getKod();
  }

  private String getFirstVardformKod(PrivatlakareEntity entity) {
    if (entity == null || entity.getVardformer() == null || entity.getVardformer().isEmpty()) {
      return null;
    }

    final var typeOfCare = entity.getVardformer().getFirst();
    return typeOfCare == null ? null : typeOfCare.getKod();
  }

  private List<Speciality> convertSpecialiteter(List<SpecialitetEntity> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream()
        .map(s -> new Speciality(s.getKod(), s.getNamn()))
        .toList();
  }

  private List<LicensedHealtcareProfession> convertLegitimeradeYrkesgrupper(
      List<LegitimeradYrkesgruppEntity> entities) {
    if (entities == null) {
      return List.of();
    }

    return entities.stream()
        .map(l -> new LicensedHealtcareProfession(l.getKod(), l.getNamn()))
        .toList();
  }
}