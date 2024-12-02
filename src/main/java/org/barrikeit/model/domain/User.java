package org.barrikeit.model.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
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
@Table(name = EntityConstants.USERS)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_USER, nullable = false))
public class User extends GenericEntity {
  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "email", length = 50)
  private String email;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(id, user.id)
        && Objects.equals(username, user.username)
        && Objects.equals(email, user.email);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "User{" + "id=" + id + '}';
  }
}
