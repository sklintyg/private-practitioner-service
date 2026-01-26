package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by pebe on 2015-06-24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRIVATLAKARE")
public class PrivatlakareEntity {

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

  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "privatlakareId")
  private List<EpostEntity> epostList;

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

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  private List<BefattningEntity> befattningar;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  @Builder.Default
  private List<LegitimeradYrkesgruppEntity> legitimeradeYrkesgrupper = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  @OrderBy("namn ASC")
  @Builder.Default
  private List<SpecialitetEntity> specialiteter = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  @OrderBy("kod ASC")
  @Builder.Default
  private List<RestriktionEntity> restriktioner = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  private List<VerksamhetstypEntity> verksamhetstyper;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  private List<VardformEntity> vardformer;

  @Column(name = "SENASTE_HOSP_UPPDATERING", nullable = true)
  private LocalDateTime senasteHospUppdatering;

  @Column(name = "REGISTRERINGSDATUM", nullable = false)
  private LocalDateTime registreringsdatum;

  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  private Set<MedgivandeEntity> medgivandeEntity;

  /**
   * Update Befattningar with a new Befattningskod if the Set exists. Effectively overriding the
   * one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateBefattningar(String kod) {
    if (this.getBefattningar() != null && !this.getBefattningar().isEmpty()) {
      this.befattningar.getFirst().setKod(kod);
    } else {
      this.befattningar = new ArrayList<>();
      this.befattningar.add(new BefattningEntity(kod));
    }
  }

  /**
   * Update Verksamhetstyper with a new Verksamhetstyp-kod if the Set exists. Effectively overriding
   * the one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateVerksamhetstyper(String kod) {
    if (this.getVerksamhetstyper() != null && !this.getVerksamhetstyper().isEmpty()) {
      this.verksamhetstyper.getFirst().setKod(kod);
    } else {
      this.verksamhetstyper = new ArrayList<>();
      this.verksamhetstyper.add(new VerksamhetstypEntity(kod));
    }
  }

  /**
   * Update Vardformer with a new Vardform-kod if the Set exists. Effectively overriding the
   * one-to-many cardinality of this field and enforcing a one-to-one behavior
   */
  public void updateVardformer(String kod) {
    if (this.getVardformer() != null && !this.getVardformer().isEmpty()) {
      this.vardformer.getFirst().setKod(kod);
    } else {
      this.vardformer = new ArrayList<>();
      this.vardformer.add(new VardformEntity(kod));
    }
  }

  public void setSpecialiteter(List<SpecialitetEntity> specialiteter) {
    this.specialiteter.clear();
    if (specialiteter != null) {
      this.specialiteter.addAll(specialiteter);
    }
  }

  public void setRestriktioner(List<RestriktionEntity> restriktioner) {
    this.restriktioner.clear();
    if (restriktioner != null) {
      this.restriktioner.addAll(restriktioner);
    }
  }

  public void setLegitimeradeYrkesgrupper(
      List<LegitimeradYrkesgruppEntity> legitimeradeYrkesgrupper) {
    this.legitimeradeYrkesgrupper.clear();
    if (legitimeradeYrkesgrupper != null) {
      this.legitimeradeYrkesgrupper.addAll(legitimeradeYrkesgrupper);
    }
  }
}
