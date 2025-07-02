package org.barrikeit.model.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class GenericEntity<I extends Serializable>
    implements Serializable, Persistable<I> {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected I id;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GenericEntity<? extends Serializable> that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Generic{" + "id=" + id + '}';
  }

  @Override
  public boolean isNew() {
    return getId() == null;
  }
}
