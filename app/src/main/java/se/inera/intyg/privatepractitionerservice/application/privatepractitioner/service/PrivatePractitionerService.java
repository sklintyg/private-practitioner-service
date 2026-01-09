package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.converter.PrivatePractitionerConverter;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Service
@RequiredArgsConstructor
public class PrivatePractitionerService {

  private final PrivatePractitionerRepository privatePractitionerRepository;
  private final PrivatePractitionerConverter privatePractitionerConverter;
  private final UpdatePrivatePractitionerFromPUService updatePrivatePractitionerFromPUService;

  public PrivatePractitionerDTO getPrivatePractitioner(String personOrHsaId) {
    if (personOrHsaId == null) {
      return null;
    }

    return privatePractitionerRepository.findByPersonId(personOrHsaId)
        .map(updatePrivatePractitionerFromPUService::updateFromPu)
        .map(privatePractitionerConverter::convert)
        .orElseGet(() -> privatePractitionerRepository.findByHsaId(personOrHsaId)
            .map(privatePractitionerConverter::convert)
            .orElse(null)
        );
  }

  public List<PrivatePractitionerDTO> getPrivatePractitioners() {
    return privatePractitionerRepository.findAll().stream()
        .map(privatePractitionerConverter::convert)
        .toList();
  }
}
