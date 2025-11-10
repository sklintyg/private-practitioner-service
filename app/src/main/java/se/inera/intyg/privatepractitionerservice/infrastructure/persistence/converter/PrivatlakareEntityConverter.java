package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter;

import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

@Component
public class PrivatlakareEntityConverter {

  public PrivatePractitioner convert(PrivatlakareEntity privatlakareEntity) {
    return PrivatePractitioner.builder()
        .hsaId(privatlakareEntity.getHsaId())
        .personId(privatlakareEntity.getPersonId())
        .name(privatlakareEntity.getFullstandigtNamn())
        .careProviderName(privatlakareEntity.getVardgivareNamn())
        .email(privatlakareEntity.getEpost())
        .registrationDate(privatlakareEntity.getRegistreringsdatum())
        .consentFormVersion(
            privatlakareEntity.getMedgivande() == null ? null
                : privatlakareEntity.getMedgivande().stream().findFirst()
                    .map(consent -> consent.getMedgivandeText().getVersion())
                    .orElse(null)
        )
        .build();
  }
}
