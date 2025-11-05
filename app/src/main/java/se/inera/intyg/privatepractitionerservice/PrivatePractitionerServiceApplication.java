package se.inera.intyg.privatepractitionerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(
    basePackages = {
        "se.inera.intyg.privatepractitionerservice",
        "se.inera.intyg.privatlakarportal"
    }
)
public class PrivatePractitionerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PrivatePractitionerServiceApplication.class, args);
  }
}