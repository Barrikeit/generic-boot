package org.barrikeit.model.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.barrikeit.util.constants.EntityConstants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = EntityConstants.LOCATIONS)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_LOCATION, nullable = false))
@AttributeOverride(
    name = EntityConstants.CODE,
    column = @Column(name = EntityConstants.CODE_LOCATION, nullable = false))
public class Location extends GenericCodeEntity<Long, UUID> {
  @Serial private static final long serialVersionUID = 1L;

  @NotNull
  @Size(max = 255)
  @Column(name = "location", nullable = false)
  private String name;

  @NotNull
  @Size(max = 255)
  @Column(name = "country", nullable = false)
  private String country;

  @Size(max = 255)
  @Column(name = "city")
  private String city;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Location e)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(code, e.code) && Objects.equals(name, e.name);
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
