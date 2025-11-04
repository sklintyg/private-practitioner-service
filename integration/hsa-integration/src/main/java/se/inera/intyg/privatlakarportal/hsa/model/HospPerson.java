package se.inera.intyg.privatlakarportal.hsa.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HospPerson {

  String personalIdentityNumber;
  String personalPrescriptionCode;
  List<String> specialityNames = new ArrayList<>();
  List<String> specialityCodes = new ArrayList<>();
  List<String> hsaTitles = new ArrayList<>();
  List<String> titleCodes = new ArrayList<>();
}
