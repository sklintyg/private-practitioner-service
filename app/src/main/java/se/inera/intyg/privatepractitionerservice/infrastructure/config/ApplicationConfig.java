package se.inera.intyg.privatepractitionerservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class ApplicationConfig {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    return new CustomObjectMapper();
  }
}
