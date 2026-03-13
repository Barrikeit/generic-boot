package dev.barrikeit.model.domain.base;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
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
public abstract class GenericEmbeddedEntity<I extends Serializable> extends BaseEntity
    implements Serializable, Persistable<I> {

  @Serial private static final long serialVersionUID = 1L;

  @EmbeddedId protected I id;

  @Override
  public boolean isNew() {
    return id == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GenericEmbeddedEntity<?> that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "GenericEmbedded{" + "id=" + id + '}';
  }
}
