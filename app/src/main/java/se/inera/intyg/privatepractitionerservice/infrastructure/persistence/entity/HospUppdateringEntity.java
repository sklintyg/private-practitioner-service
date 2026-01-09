package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by pebe on 2015-09-03.
 */
@Data
@AllArgsConstructor
@Entity
@Table(name = "HOSP_UPPDATERING")
public class HospUppdateringEntity {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "SENASTE_HOSP_UPPDATERING", nullable = true)
  private LocalDateTime senasteHospUppdatering;

  public HospUppdateringEntity() {
    id = 1L;
  }

  public HospUppdateringEntity(LocalDateTime senasteHospUppdatering) {
    id = 1L;
    this.senasteHospUppdatering = senasteHospUppdatering;
  }

}
