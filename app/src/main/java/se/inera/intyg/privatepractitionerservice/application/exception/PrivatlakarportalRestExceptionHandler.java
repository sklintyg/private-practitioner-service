package se.inera.intyg.privatepractitionerservice.application.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PrivatlakarportalRestExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(
      PrivatlakarportalRestExceptionHandler.class);

  @ExceptionHandler(PrivatlakarportalServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public PrivatlakarportalRestExceptionResponse handleServiceException(HttpServletRequest request,
      PrivatlakarportalServiceException e) {
    LOG.warn("Internal exception occured! Internal error code: {} Error message: {}",
        e.getErrorCode(),
        e.getMessage());
    return new PrivatlakarportalRestExceptionResponse(e.getErrorCode(), e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public PrivatlakarportalRestExceptionResponse handleRuntimeException(HttpServletRequest request,
      RuntimeException re) {
    LOG.error("Unhandled RuntimeException occured!", re);
    return new PrivatlakarportalRestExceptionResponse(
        PrivatlakarportalErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unhandled runtime exception");
  }

}
