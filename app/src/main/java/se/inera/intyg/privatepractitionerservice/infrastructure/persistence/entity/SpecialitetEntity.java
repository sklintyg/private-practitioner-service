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

/**
 * Created by pebe on 2015-06-24.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SPECIALITET")
public class SpecialitetEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "KOD", nullable = false)
  private String kod;

  @Column(name = "NAMN", nullable = false)
  private String namn;

  public SpecialitetEntity(String namn, String kod) {
    this.namn = namn;
    this.kod = kod;
  }
}
