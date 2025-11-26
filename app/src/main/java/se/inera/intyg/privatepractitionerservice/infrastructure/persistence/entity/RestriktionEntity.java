package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "RESTRIKTION")
public class RestriktionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "KOD", nullable = false)
  private String kod;

  @Column(name = "NAMN", nullable = false)
  private String namn;

  public RestriktionEntity(String kod, String namn) {
    this.kod = kod;
    this.namn = namn;
  }
}
