package se.inera.intyg.privatepractitionerservice.testability.service.converter;


import java.util.List;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.CodeDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.dto.PrivatePractitionerDTO;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.LicensedHealtcareProfession;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.PrivatePractitioner;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model.Speciality;

@Component
public class TestabilityPrivatePractitionerConverter {

  public PrivatePractitionerDTO convert(PrivatePractitioner privatePractitioner) {
    if (privatePractitioner == null) {
      return null;
    }

    return PrivatePractitionerDTO.builder()
        .hsaId(privatePractitioner.getHsaId())
        .personId(privatePractitioner.getPersonId())
        .name(privatePractitioner.getName())
        .email(privatePractitioner.getEmail())
        .careProviderName(privatePractitioner.getCareProviderName())
        .registrationDate(privatePractitioner.getRegistrationDate())
        .position(privatePractitioner.getPosition())
        .careUnitName(privatePractitioner.getCareUnitName())
        .healthcareServiceType(privatePractitioner.getHealthcareServiceType())
        .typeOfCare(privatePractitioner.getTypeOfCare())
        .workplaceCode(privatePractitioner.getWorkplaceCode())
        .phoneNumber(privatePractitioner.getPhoneNumber())
        .address(privatePractitioner.getAddress())
        .zipCode(privatePractitioner.getZipCode())
        .city(privatePractitioner.getCity())
        .municipality(privatePractitioner.getMunicipality())
        .county(privatePractitioner.getCounty())
        .personalPrescriptionCode(privatePractitioner.getPersonalPrescriptionCode())
        .specialties(convertSpecialities(privatePractitioner.getSpecialties()))
        .licensedHealthcareProfessions(
            convertLicensed(privatePractitioner.getLicensedHealthcareProfessions())
        )
        .build();
  }

  private List<CodeDTO> convertSpecialities(
      List<Speciality> specialities) {
    if (specialities == null) {
      return List.of();
    }

    return specialities.stream()
        .map(
            s -> new CodeDTO(s.code(), s.name())
        )
        .toList();
  }

  private List<CodeDTO> convertLicensed(
      List<LicensedHealtcareProfession> licensed) {
    if (licensed == null) {
      return List.of();
    }

    return licensed.stream()
        .map(
            l -> new CodeDTO(l.code(), l.name())
        )
        .toList();
  }
}
