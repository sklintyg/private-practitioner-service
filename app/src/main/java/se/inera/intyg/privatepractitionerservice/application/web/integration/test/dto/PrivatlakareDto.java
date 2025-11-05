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
package se.inera.intyg.privatepractitionerservice.application.web.integration.test.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.model.Privatlakare;

public class PrivatlakareDto {

  private String privatlakareId;

  private String personId;

  private String hsaId;

  private String fullstandigtNamn;

  private String forskrivarKod;

  private boolean godkandAnvandare;

  private String enhetsId;

  private String enhetsNamn;

  private String arbetsplatsKod;

  private String agarform;

  private String postadress;

  private String postnummer;

  private String postort;

  private String telefonnummer;

  private String epost;

  private LocalDateTime enhetStartdatum;

  private LocalDateTime enhetSlutDatum;

  private String lan;

  private String kommun;

  private String vardgivareId;

  private String vardgivareNamn;

  private LocalDateTime vardgivareStartdatum;

  private LocalDateTime vardgivareSlutdatum;

  private Set<BefattningDto> befattningar;

  private Set<LegitimeradYrkesgruppDto> legitimeradeYrkesgrupper;

  private List<SpecialitetDto> specialiteter;

  private Set<VerksamhetstypDto> verksamhetstyper;

  private Set<VardformDto> vardformer;

  private LocalDateTime senasteHospUppdatering;

  private Set<MedgivandeDto> medgivande;

  private LocalDateTime registreringsdatum;

  public PrivatlakareDto() {

  }

  public PrivatlakareDto(Privatlakare entity) {
    this.agarform = entity.getAgarform();
    this.arbetsplatsKod = entity.getArbetsplatsKod();
    this.enhetsId = entity.getEnhetsId();
    this.enhetsNamn = entity.getEnhetsNamn();
    this.enhetStartdatum = entity.getEnhetStartdatum();
    this.enhetSlutDatum = entity.getEnhetSlutDatum();
    this.epost = entity.getEpost();
    this.forskrivarKod = entity.getForskrivarKod();
    this.fullstandigtNamn = entity.getFullstandigtNamn();
    this.godkandAnvandare = entity.isGodkandAnvandare();
    this.hsaId = entity.getHsaId();
    this.kommun = entity.getKommun();
    this.lan = entity.getLan();
    this.personId = entity.getPersonId();
    this.postadress = entity.getPostadress();
    this.postnummer = entity.getPostnummer();
    this.postort = entity.getPostort();
    this.privatlakareId = entity.getPrivatlakareId();
    this.registreringsdatum = entity.getRegistreringsdatum();
    this.senasteHospUppdatering = entity.getSenasteHospUppdatering();
    this.telefonnummer = entity.getTelefonnummer();
    this.vardgivareId = entity.getVardgivareId();
    this.vardgivareNamn = entity.getVardgivareNamn();
    this.vardgivareStartdatum = entity.getVardgivareStartdatum();
    this.vardgivareSlutdatum = entity.getVardgivareSlutdatum();

    this.vardformer = entity.getVardformer().stream().map(vf -> new VardformDto(vf))
        .collect(Collectors.toSet());
    this.specialiteter = entity.getSpecialiteter().stream().map(sp -> new SpecialitetDto(sp))
        .collect(Collectors.toList());
    this.legitimeradeYrkesgrupper = entity.getLegitimeradeYrkesgrupper().stream()
        .map(ly -> new LegitimeradYrkesgruppDto(ly))
        .collect(Collectors.toSet());

    this.medgivande = entity.getMedgivande().stream().map(m -> new MedgivandeDto(m))
        .collect(Collectors.toSet());
    this.befattningar = entity.getBefattningar().stream().map(b -> new BefattningDto(b))
        .collect(Collectors.toSet());
    this.verksamhetstyper = entity.getVerksamhetstyper().stream().map(v -> new VerksamhetstypDto(v))
        .collect(Collectors.toSet());
  }

  public String getPrivatlakareId() {
    return privatlakareId;
  }

  public void setPrivatlakareId(String privatlakareId) {
    this.privatlakareId = privatlakareId;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public String getHsaId() {
    return hsaId;
  }

  public void setHsaId(String hsaId) {
    this.hsaId = hsaId;
  }

  public String getFullstandigtNamn() {
    return fullstandigtNamn;
  }

  public void setFullstandigtNamn(String fullstandigtNamn) {
    this.fullstandigtNamn = fullstandigtNamn;
  }

  public String getForskrivarKod() {
    return forskrivarKod;
  }

  public void setForskrivarKod(String forskrivarKod) {
    this.forskrivarKod = forskrivarKod;
  }

  public boolean isGodkandAnvandare() {
    return godkandAnvandare;
  }

  public void setGodkandAnvandare(boolean godkandAnvandare) {
    this.godkandAnvandare = godkandAnvandare;
  }

  public String getEnhetsId() {
    return enhetsId;
  }

  public void setEnhetsId(String enhetsId) {
    this.enhetsId = enhetsId;
  }

  public String getEnhetsNamn() {
    return enhetsNamn;
  }

  public void setEnhetsNamn(String enhetsNamn) {
    this.enhetsNamn = enhetsNamn;
  }

  public String getArbetsplatsKod() {
    return arbetsplatsKod;
  }

  public void setArbetsplatsKod(String arbetsplatsKod) {
    this.arbetsplatsKod = arbetsplatsKod;
  }

  public String getAgarform() {
    return agarform;
  }

  public void setAgarform(String agarform) {
    this.agarform = agarform;
  }

  public String getPostadress() {
    return postadress;
  }

  public void setPostadress(String postadress) {
    this.postadress = postadress;
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

  public LocalDateTime getEnhetStartdatum() {
    return enhetStartdatum;
  }

  public void setEnhetStartdatum(LocalDateTime enhetStartdatum) {
    this.enhetStartdatum = enhetStartdatum;
  }

  public LocalDateTime getEnhetSlutDatum() {
    return enhetSlutDatum;
  }

  public void setEnhetSlutDatum(LocalDateTime enhetSlutDatum) {
    this.enhetSlutDatum = enhetSlutDatum;
  }

  public String getLan() {
    return lan;
  }

  public void setLan(String lan) {
    this.lan = lan;
  }

  public String getKommun() {
    return kommun;
  }

  public void setKommun(String kommun) {
    this.kommun = kommun;
  }

  public String getVardgivareId() {
    return vardgivareId;
  }

  public void setVardgivareId(String vardgivareId) {
    this.vardgivareId = vardgivareId;
  }

  public String getVardgivareNamn() {
    return vardgivareNamn;
  }

  public void setVardgivareNamn(String vardgivareNamn) {
    this.vardgivareNamn = vardgivareNamn;
  }

  public LocalDateTime getVardgivareStartdatum() {
    return vardgivareStartdatum;
  }

  public void setVardgivareStartdatum(LocalDateTime vardgivareStartdatum) {
    this.vardgivareStartdatum = vardgivareStartdatum;
  }

  public LocalDateTime getVardgivareSlutdatum() {
    return vardgivareSlutdatum;
  }

  public void setVardgivareSlutdatum(LocalDateTime vardgivareSlutdatum) {
    this.vardgivareSlutdatum = vardgivareSlutdatum;
  }

  public LocalDateTime getSenasteHospUppdatering() {
    return senasteHospUppdatering;
  }

  public void setSenasteHospUppdatering(LocalDateTime senasteHospUppdatering) {
    this.senasteHospUppdatering = senasteHospUppdatering;
  }

  public LocalDateTime getRegistreringsdatum() {
    return registreringsdatum;
  }

  public void setRegistreringsdatum(LocalDateTime registreringsdatum) {
    this.registreringsdatum = registreringsdatum;
  }

  public Set<BefattningDto> getBefattningar() {
    return befattningar;
  }

  public void setBefattningar(Set<BefattningDto> befattningar) {
    this.befattningar = befattningar;
  }

  public Set<LegitimeradYrkesgruppDto> getLegitimeradeYrkesgrupper() {
    return legitimeradeYrkesgrupper;
  }

  public void setLegitimeradeYrkesgrupper(Set<LegitimeradYrkesgruppDto> legitimeradeYrkesgrupper) {
    this.legitimeradeYrkesgrupper = legitimeradeYrkesgrupper;
  }

  public List<SpecialitetDto> getSpecialiteter() {
    return specialiteter;
  }

  public void setSpecialiteter(List<SpecialitetDto> specialiteter) {
    this.specialiteter = specialiteter;
  }

  public Set<VerksamhetstypDto> getVerksamhetstyper() {
    return verksamhetstyper;
  }

  public void setVerksamhetstyper(Set<VerksamhetstypDto> verksamhetstyper) {
    this.verksamhetstyper = verksamhetstyper;
  }

  public Set<VardformDto> getVardformer() {
    return vardformer;
  }

  public void setVardformer(Set<VardformDto> vardformer) {
    this.vardformer = vardformer;
  }

  public Set<MedgivandeDto> getMedgivande() {
    return medgivande;
  }

  public void setMedgivande(Set<MedgivandeDto> medgivande) {
    this.medgivande = medgivande;
  }
}
