package dev.barrikeit.model.domain;

import dev.barrikeit.springframework.data.entity.GenericCodeEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = EntityConstants.LOCATIONS)
public class Location extends GenericCodeEntity<Long, String> {

  @NotNull
  @Size(max = 255)
  @Column(name = EntityConstants.COUNTRY, nullable = false)
  private String country;

  @Size(max = 255)
  @Column(name = EntityConstants.CITY)
  private String city;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location e)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(code, e.code) && Objects.equals(country, e.country);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (code != null ? code.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Location{" + "code='" + code + '\'' + '}';
  }
}
