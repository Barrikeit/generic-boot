package org.barrikeit.model.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDateTime;
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
@Table(name = EntityConstants.USERS)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_USER, nullable = false))
public class User extends GenericEntity<Long> {
  @Serial private static final long serialVersionUID = 1L;

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "email", length = 50, nullable = false, unique = true)
  private String email;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = false;

  @Column(name = "banned", nullable = false)
  private boolean banned = false;

  @Column(name = "ban_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime banDate;

  @Column(name = "login_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime loginDate;

  @Column(name = "login_attempts")
  private Integer loginAttempts;

  @ManyToMany
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_role"))
  private Set<Role> roles = new LinkedHashSet<>();

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
