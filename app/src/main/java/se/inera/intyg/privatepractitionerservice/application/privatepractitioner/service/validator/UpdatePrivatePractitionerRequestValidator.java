package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalErrorCodeEnum;
import se.inera.intyg.privatepractitionerservice.application.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.UpdatePrivatePractitionerRequest;

@Component
public class UpdatePrivatePractitionerRequestValidator {

  public void validate(UpdatePrivatePractitionerRequest updatePrivatePractitionerRequest) {
    throw new PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum.BAD_REQUEST, "Not implemented yet");
  }
}
