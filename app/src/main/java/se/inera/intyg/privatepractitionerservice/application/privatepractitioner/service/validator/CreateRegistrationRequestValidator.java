package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CreateRegistrationRequest;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository.PrivatePractitionerRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateRegistrationRequestValidator {

  private final PrivatePractitionerRepository privatePractitionerRepository;

  public void validate(CreateRegistrationRequest registration) {
    if (!checkIsValid(registration)) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "CreateRegistrationRequest is not valid"
      );
    }

    if (registration.getConsentFormVersion() == null || registration.getConsentFormVersion() <= 0) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.BAD_REQUEST,
          "Not allowed to create registration without medgivande"
      );
    }

    if (privatePractitionerRepository.isExists(registration.getPersonId())) {
      throw new PrivatlakarportalServiceException(
          PrivatlakarportalErrorCodeEnum.ALREADY_EXISTS,
          "Registration already exists"
      );
    }
  }

  private boolean checkIsValid(CreateRegistrationRequest registration) {
    if (registration == null) {
      return false;
    }

    return checkValues(
        registration.getPosition(),
        registration.getCareUnitName(),
        registration.getOwnershipType(),
        registration.getTypeOfCare(),
        registration.getHealthcareServiceType(),
        registration.getPhoneNumber(),
        registration.getEmail(),
        registration.getAddress(),
        registration.getZipCode(),
        registration.getCity(),
        registration.getMunicipality(),
        registration.getCounty()
    );
  }

  private boolean checkValues(String... strings) {
    return Stream.of(strings).noneMatch(StringUtils::isBlank);
  }
}
