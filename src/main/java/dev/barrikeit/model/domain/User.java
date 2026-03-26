package dev.barrikeit.model.domain;

import dev.barrikeit.model.domain.base.GenericEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = EntityConstants.USERS)
public class User extends GenericEntity<UUID> {

  @NotNull
  @Size(max = 50)
  @Column(name = EntityConstants.USERNAME, nullable = false, length = 50, unique = true)
  private String username;

  @Size(max = 50)
  @Column(name = EntityConstants.NAME, length = 50)
  private String name;

  @Size(max = 50)
  @Column(name = EntityConstants.SURNAME1, length = 50)
  private String surname1;

  @Size(max = 50)
  @Column(name = EntityConstants.SURNAME2, length = 50)
  private String surname2;

  @NotNull
  @Size(max = 100)
  @Column(name = EntityConstants.EMAIL, nullable = false, length = 100, unique = true)
  private String email;

  @Size(max = 50)
  @Column(name = EntityConstants.PHONE, length = 50)
  private String phone;

  @NotNull
  @Size(max = 255)
  @Column(name = EntityConstants.PASSWORD, nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.ID_DIRECTION)
  private Direction direction;

  @OneToOne(mappedBy = EntityConstants.MAPS_ID, cascade = CascadeType.ALL, optional = false)
  private UserSecurity security;

  @ManyToMany
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = EntityConstants.ID_USER),
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
