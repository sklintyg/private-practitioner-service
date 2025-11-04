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

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatlakarportal.persistence.model.Privatlakare;
import se.inera.intyg.privatlakarportal.persistence.repository.PrivatlakareRepository;
import se.inera.intyg.privatlakarportal.service.model.PrivatePractitioner;

@Service
public class PrivatePractitionerServiceImpl implements PrivatePractitionerService {

  private static final String PNR_PATTERN = "\\d{12}";

  private final PrivatlakareRepository privatlakareRepository;

  @Autowired
  public PrivatePractitionerServiceImpl(PrivatlakareRepository privatlakareRepository) {
    this.privatlakareRepository = privatlakareRepository;
  }

  @Override
  public PrivatePractitioner getPrivatePractitioner(String personOrHsaId) {
    if (personOrHsaId == null) {
      return null;
    }

    Privatlakare privatlakare = null;

    if (personOrHsaId.matches(PNR_PATTERN)) {
      // try to find with PNR first
      privatlakare = privatlakareRepository.findByPersonId(personOrHsaId);
    }

    if (privatlakare == null) {
      privatlakare = privatlakareRepository.findByHsaId(personOrHsaId);
    }
    return convert(privatlakare);
  }

  @Override
  public List<PrivatePractitioner> getPrivatePractitioners() {
    final var allPrivatlakare = privatlakareRepository.findAll();

    if (allPrivatlakare.isEmpty()) {
      return List.of();
    }

    return allPrivatlakare.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  private PrivatePractitioner convert(Privatlakare privatlakare) {

    if (privatlakare == null) {
      return null;
    }

    return new PrivatePractitioner(privatlakare.getHsaId(), privatlakare.getPersonId(),
        privatlakare.getFullstandigtNamn(),
        privatlakare.getVardgivareNamn(), privatlakare.getEpost(),
        privatlakare.getRegistreringsdatum());
  }
}
