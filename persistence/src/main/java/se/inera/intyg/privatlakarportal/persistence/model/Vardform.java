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

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Created by pebe on 2015-06-24.
 */
@Entity
@Table(name = "VARDFORM")
public class Vardform {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRIVATLAKARE_ID", nullable = false)
  @JsonBackReference
  private Privatlakare privatlakare;

  @Column(name = "KOD", nullable = false)
  private String kod;

  public Vardform() {
  }

  public Vardform(Privatlakare privatlakare, String kod) {
    this.privatlakare = privatlakare;
    this.kod = kod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (!(o instanceof Vardform)) {
      return false;
    } else {
      Vardform other = (Vardform) o;

      if (id == null) {
        return false;
      } else {
        return id.equals(other.id);
      }
    }
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  public Privatlakare getPrivatlakare() {
    return privatlakare;
  }

  public void setPrivatlakare(Privatlakare privatlakare) {
    this.privatlakare = privatlakare;
  }

  public String getKod() {
    return kod;
  }

  public void setKod(String kod) {
    this.kod = kod;
  }

}
