package dev.barrikeit.model.domain;

import dev.barrikeit.model.domain.base.GenericEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;
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
@Table(name = EntityConstants.DIRECTIONS)
public class Direction extends GenericEntity<UUID> {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.ID_LOCATION, nullable = false)
  private Location location;

  @NotNull
  @Size(max = 255)
  @Column(name = EntityConstants.STREET, nullable = false)
  private String street;

  @NotNull
  @Size(max = 20)
  @Column(name = EntityConstants.NUMBER, nullable = false, length = 20)
  private String number;

  @Size(max = 20)
  @Column(name = EntityConstants.POSTAL_CODE, length = 20)
  private String postalCode;

  @Size(max = 255)
  @Column(name = EntityConstants.EXTRA)
  private String extra;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Direction e)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(id, e.id)
        && Objects.equals(street, e.street)
        && Objects.equals(number, e.number);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Direction{" + "id=" + id + '}';
  }
}
