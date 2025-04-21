package org.barrikeit.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.barrikeit.util.constants.EntityConstants;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = EntityConstants.MODULES)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_MODULE, nullable = false))
@AttributeOverride(
    name = EntityConstants.CODE,
    column =
        @Column(name = EntityConstants.CODE_MODULE, length = 3, nullable = false, unique = true))
public class Module extends GenericCodeEntity<Integer, String> {
  @Serial private static final long serialVersionUID = 1L;

  @Size(max = 200)
  @NotNull
  @Column(name = "module", nullable = false, length = 200)
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Module e)) return false;
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
    return "Module{" + "code='" + code + '\'' + '}';
  }
}
