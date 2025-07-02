package org.barrikeit.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
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
@Table(name = EntityConstants.ROLES)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_ROLE, nullable = false))
@AttributeOverride(
    name = EntityConstants.CODE,
    column = @Column(name = EntityConstants.CODE_ROLE, length = 2, nullable = false, unique = true))
public class Role extends GenericCodeEntity<Integer, String> {
  @Serial private static final long serialVersionUID = 1L;

  @NotNull
  @Size(max = 50)
  @Column(name = "role", length = 50, nullable = false)
  private String name;

  @ManyToMany
  @JoinTable(
      name = "role_modules",
      joinColumns = @JoinColumn(name = "id_role"),
      inverseJoinColumns = @JoinColumn(name = "id_module"))
  private Set<Module> modules = new LinkedHashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role e)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(code, e.code) && Objects.equals(name, e.name);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Role{" + "code=" + code + '}';
  }
}
