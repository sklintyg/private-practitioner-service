package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HospPerson {

  String personalIdentityNumber;
  String personalPrescriptionCode;
  List<String> specialityNames = new ArrayList<>();
  List<String> specialityCodes = new ArrayList<>();
  List<String> hsaTitles = new ArrayList<>();
  List<String> titleCodes = new ArrayList<>();
}
