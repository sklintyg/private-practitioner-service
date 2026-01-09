package se.inera.intyg.privatepractitionerservice.application.exception;

public record PrivatlakarportalRestExceptionResponse(PrivatlakarportalErrorCodeEnum errorCode,
                                                     String message) {

}
