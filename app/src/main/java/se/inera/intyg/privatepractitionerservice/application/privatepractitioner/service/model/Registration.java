package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

  private String personId;
  private String name;

  private String befattning;
  private String verksamhetensNamn;
  @Builder.Default
  private String agarForm = "Privat";
  private String vardform;
  private String verksamhetstyp;
  private String arbetsplatskod;

  private String telefonnummer;
  private String epost;
  private String adress;
  private String postnummer;
  private String postort;
  private String kommun;
  private String lan;

  private Long godkannandeMedgivandeVersion;

  public boolean checkIsValid() {
    return checkValues(
        befattning,
        verksamhetensNamn,
        agarForm,
        vardform,
        verksamhetstyp,
        telefonnummer,
        epost,
        adress,
        postnummer,
        postort,
        kommun,
        lan
    );
  }

  private boolean checkValues(String... strings) {
    return Stream.of(strings).noneMatch(StringUtils::isBlank);
  }
}
