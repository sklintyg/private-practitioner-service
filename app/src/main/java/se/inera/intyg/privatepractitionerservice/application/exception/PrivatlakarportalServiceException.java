package se.inera.intyg.privatepractitionerservice.application.exception;

import lombok.Getter;

@Getter
public class PrivatlakarportalServiceException extends RuntimeException {

  private final PrivatlakarportalErrorCodeEnum errorCode;

  public PrivatlakarportalServiceException(PrivatlakarportalErrorCodeEnum errorCode,
      String message) {
    super(message);
    this.errorCode = errorCode;
  }

}
