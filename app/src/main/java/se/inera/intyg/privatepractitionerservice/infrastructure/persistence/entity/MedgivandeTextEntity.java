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
package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by pebe on 2015-09-09.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MEDGIVANDETEXT")
public class MedgivandeTextEntity {

  @Id
  @Column(name = "VERSION")
  private Long version;

  @Lob
  @Column(name = "MEDGIVANDE_TEXT")
  private String medgivandeText;

  @Column(name = "DATUM", nullable = true)
  private LocalDateTime datum;

  @JsonManagedReference(value = "medgivandeText")
  @OneToMany(mappedBy = "medgivandeText", cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private Set<MedgivandeEntity> medgivande;
}
