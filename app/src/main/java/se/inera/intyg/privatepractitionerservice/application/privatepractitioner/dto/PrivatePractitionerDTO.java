package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO.PrivatePractitionerDTOBuilder;

@JsonDeserialize(builder = PrivatePractitionerDTOBuilder.class)
@Value
@Builder
public class PrivatePractitionerDTO {

  String hsaId;
  String personId;
  String name;
  String careProviderName;
  String email;
  LocalDateTime registrationDate;

  String position;
  String careUnitName;
  String typeOfCare;
  String healthcareServiceType;
  String workplaceCode;

  String phoneNumber;
  String address;
  String zipCode;
  String city;
  String municipality;
  String county;

  String personalPrescriptionCode;
  List<CodeDTO> specialties;
  List<CodeDTO> licensedHealthcareProfessions;

  @JsonPOJOBuilder(withPrefix = "")
  public static class PrivatePractitionerDTOBuilder {

  }
}