/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pebe on 2015-08-06.
 */
public class Registration {

  // Step 1
  private String befattning;
  private String verksamhetensNamn;
  private String agarForm;
  private String vardform;
  private String verksamhetstyp;
  private String arbetsplatskod;

  // Step 2
  private String telefonnummer;
  private String epost;
  private String adress;
  private String postnummer;
  private String postort;
  private String kommun;
  private String lan;

  public boolean checkIsValid() {
    return checkValues(befattning, verksamhetensNamn, agarForm, vardform, verksamhetstyp,
        telefonnummer, epost, adress,
        postnummer, postort, kommun, lan);
  }

  private boolean checkValues(String... strings) {
    return !Stream.of(strings).anyMatch(StringUtils::isBlank);
  }

  public String getBefattning() {
    return befattning;
  }

  public void setBefattning(String befattning) {
    this.befattning = befattning;
  }

  public String getVerksamhetensNamn() {
    return verksamhetensNamn;
  }

  public void setVerksamhetensNamn(String verksamhetensNamn) {
    this.verksamhetensNamn = verksamhetensNamn;
  }

  public String getAgarForm() {
    return agarForm;
  }

  public void setAgarForm(String agarForm) {
    this.agarForm = agarForm;
  }

  public String getVardform() {
    return vardform;
  }

  public void setVardform(String vardform) {
    this.vardform = vardform;
  }

  public String getVerksamhetstyp() {
    return verksamhetstyp;
  }

  public void setVerksamhetstyp(String verksamhetstyp) {
    this.verksamhetstyp = verksamhetstyp;
  }

  public String getArbetsplatskod() {
    return arbetsplatskod;
  }

  public void setArbetsplatskod(String arbetsplatskod) {
    this.arbetsplatskod = arbetsplatskod;
  }

  public String getTelefonnummer() {
    return telefonnummer;
  }

  public void setTelefonnummer(String telefonnummer) {
    this.telefonnummer = telefonnummer;
  }

  public String getEpost() {
    return epost;
  }

  public void setEpost(String epost) {
    this.epost = epost;
  }

  public String getAdress() {
    return adress;
  }

  public void setAdress(String adress) {
    this.adress = adress;
  }

  public String getPostnummer() {
    return postnummer;
  }

  public void setPostnummer(String postnummer) {
    this.postnummer = postnummer;
  }

  public String getPostort() {
    return postort;
  }

  public void setPostort(String postort) {
    this.postort = postort;
  }

  public String getKommun() {
    return kommun;
  }

  public void setKommun(String kommun) {
    this.kommun = kommun;
  }

  public String getLan() {
    return lan;
  }

  public void setLan(String lan) {
    this.lan = lan;
  }
}
