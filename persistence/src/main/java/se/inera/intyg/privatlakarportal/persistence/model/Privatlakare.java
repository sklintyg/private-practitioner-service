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
package se.inera.intyg.privatlakarportal.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by pebe on 2015-06-24.
 */
@Entity
@Table(name = "PRIVATLAKARE")
public class Privatlakare {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  @Column(name = "PRIVATLAKARE_ID", nullable = false)
  private String privatlakareId;

  @Column(name = "PERSONID", nullable = false)
  private String personId;

  @Column(name = "HSAID", nullable = false)
  private String hsaId;

  @Column(name = "FULLSTANDIGT_NAMN", nullable = false)
  private String fullstandigtNamn;

  @Column(name = "FORSKRIVARKOD", nullable = true)
  private String forskrivarKod;

  @Column(name = "GODKAND_ANVANDARE", nullable = false)
  private boolean godkandAnvandare;

  @Column(name = "ENHETS_ID", nullable = false)
  private String enhetsId;

  @Column(name = "ENHETS_NAMN", nullable = false)
  private String enhetsNamn;

  @Column(name = "ARBETSPLATSKOD", nullable = true)
  private String arbetsplatsKod;

  @Column(name = "AGARFORM", nullable = false)
  private String agarform;

  @Column(name = "POSTADRESS", nullable = false)
  private String postadress;

  @Column(name = "POSTNUMMER", nullable = false)
  private String postnummer;

  @Column(name = "POSTORT", nullable = false)
  private String postort;

  @Column(name = "TELEFONNUMMER", nullable = false)
  private String telefonnummer;

  @Column(name = "EPOST", nullable = false)
  private String epost;

  @Column(name = "ENHET_STARTDATUM", nullable = true)
  private LocalDateTime enhetStartdatum;

  @Column(name = "ENHET_SLUTDATUM", nullable = true)
  private LocalDateTime enhetSlutDatum;

  @Column(name = "LAN", nullable = true)
  private String lan;

  @Column(name = "KOMMUN", nullable = true)
  private String kommun;

  @Column(name = "VARDGIVARE_ID", nullable = false)
  private String vardgivareId;

  @Column(name = "VARDGIVARE_NAMN", nullable = false)
  private String vardgivareNamn;

  @Column(name = "VARDGIVARE_STARTDATUM", nullable = true)

  private LocalDateTime vardgivareStartdatum;

  @Column(name = "VARDGIVARE_SLUTDATUM", nullable = true)

  private LocalDateTime vardgivareSlutdatum;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Befattning> befattningar;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<LegitimeradYrkesgrupp> legitimeradeYrkesgrupper;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("namn ASC")
  private List<Specialitet> specialiteter;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Verksamhetstyp> verksamhetstyper;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Vardform> vardformer;

  @Column(name = "SENASTE_HOSP_UPPDATERING", nullable = true)

  private LocalDateTime senasteHospUppdatering;

  @JsonManagedReference
  @OneToMany(mappedBy = "privatlakare", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Medgivande> medgivande;

  @Column(name = "REGISTRERINGSDATUM", nullable = false)

  private LocalDateTime registreringsdatum;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Privatlakare)) {
      return false;
    } else {
      Privatlakare other = (Privatlakare) o;

      if (personId == null) {
        return false;
      } else {
        return personId.equals(other.personId);
      }
    }
  }

  @Override
  public int hashCode() {
    return personId != null ? personId.hashCode() : 0;
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

  public void setArbetsplatsKod(String arbetsplaysKod) {
    this.arbetsplatsKod = arbetsplaysKod;
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

  public Set<Befattning> getBefattningar() {
    return befattningar;
  }

  public void setBefattningar(Set<Befattning> befattningar) {
    this.befattningar = befattningar;
  }

  /**
   * Update Befattningar with a new Befattningskod if the Set exists. Effectively overriding the
   * one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateBefattningar(String kod) {
    if (this.getBefattningar() != null && !this.getBefattningar().isEmpty()) {
      this.befattningar.iterator().next().setKod(kod);
    } else {
      this.befattningar = new HashSet<>();
      this.befattningar.add(new Befattning(this, kod));
    }
  }

  public Set<LegitimeradYrkesgrupp> getLegitimeradeYrkesgrupper() {
    return legitimeradeYrkesgrupper;
  }

  public void setLegitimeradeYrkesgrupper(Set<LegitimeradYrkesgrupp> legitimeradeYrkesgrupper) {
    this.legitimeradeYrkesgrupper = legitimeradeYrkesgrupper;
  }

  public List<Specialitet> getSpecialiteter() {
    return specialiteter;
  }

  public void setSpecialiteter(List<Specialitet> specialiteter) {
    this.specialiteter = specialiteter;
  }

  public Set<Verksamhetstyp> getVerksamhetstyper() {
    return verksamhetstyper;
  }

  public void setVerksamhetstyper(Set<Verksamhetstyp> verksamhetstyper) {
    this.verksamhetstyper = verksamhetstyper;
  }

  /**
   * Update Verksamhetstyper with a new Verksamhetstyp-kod if the Set exists. Effectively overriding
   * the one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateVerksamhetstyper(String kod) {
    if (this.getVerksamhetstyper() != null && !this.getVerksamhetstyper().isEmpty()) {
      this.verksamhetstyper.iterator().next().setKod(kod);
    } else {
      this.verksamhetstyper = new HashSet<>();
      this.verksamhetstyper.add(new Verksamhetstyp(this, kod));
    }
  }

  public Set<Vardform> getVardformer() {
    return vardformer;
  }

  public void setVardformer(Set<Vardform> vardformer) {
    this.vardformer = vardformer;
  }

  /**
   * Update Vardformer with a new Vardform-kod if the Set exists. Effectively overriding the
   * one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateVardformer(String kod) {
    if (this.getVardformer() != null && !this.getVardformer().isEmpty()) {
      this.vardformer.iterator().next().setKod(kod);
    } else {
      this.vardformer = new HashSet<>();
      this.vardformer.add(new Vardform(this, kod));
    }
  }

  public LocalDateTime getSenasteHospUppdatering() {
    return senasteHospUppdatering;
  }

  public void setSenasteHospUppdatering(LocalDateTime senasteHospUppdatering) {
    this.senasteHospUppdatering = senasteHospUppdatering;
  }

  public Set<Medgivande> getMedgivande() {
    return medgivande;
  }

  public void setMedgivande(Set<Medgivande> medgivande) {
    this.medgivande = medgivande;
  }

  public LocalDateTime getRegistreringsdatum() {
    return registreringsdatum;
  }

  public void setRegistreringsdatum(LocalDateTime registreringsdatum) {
    this.registreringsdatum = registreringsdatum;
  }
}
