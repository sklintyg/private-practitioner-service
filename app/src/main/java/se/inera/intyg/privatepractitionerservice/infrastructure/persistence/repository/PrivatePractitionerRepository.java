package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.converter.PrivatlakareEntityConverter;

@Repository
@RequiredArgsConstructor
public class PrivatePractitionerRepository {

  private final PrivatlakareEntityRepository privatlakareEntityRepository;
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
}
