package se.inera.intyg.privatepractitionerservice.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper extends ObjectMapper {

  private static final long serialVersionUID = 1L;

  public CustomObjectMapper() {
    setSerializationInclusion(JsonInclude.Include.ALWAYS);
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    registerModule(new JavaTimeModule());
  }
}
