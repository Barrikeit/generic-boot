package org.barrikeit.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
@AttributeOverride(
    name = EntityConstants.CODE,
    column = @Column(name = EntityConstants.CODE_USER, nullable = false))
public class User extends GenericCodeEntity<Long, UUID> {
  @Serial private static final long serialVersionUID = 1L;

  @NotNull
  @Size(max = 50)
  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String username;

  @Size(max = 50)
  @Column(name = "name", length = 50)
  private String name;

  @Size(max = 50)
  @Column(name = "surname1", length = 50)
  private String surname1;

  @Size(max = 50)
  @Column(name = "surname2", length = 50)
  private String surname2;

  @NotNull
  @Size(max = 100)
  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Size(max = 50)
  @Column(name = "phone", length = 50)
  private String phone;

  @NotNull
  @Size(max = 255)
  @Column(name = "password", nullable = false)
  private String password;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_location", referencedColumnName = "id_location")
  private Location location;

  @Column(name = "registration_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime registrationDate;

  @Size(max = 20)
  @Column(name = "verification_token")
  private String verificationToken;

  @Column(name = "enabled", nullable = false)
  private boolean enabled = false;

  @Column(name = "login_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime loginDate;

  @NotNull
  @Column(name = "login_attempts", nullable = false)
  private Integer loginAttempts = 0;

  @NotNull
  @Column(name = "banned", nullable = false)
  private boolean banned = false;

  @Column(name = "ban_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private LocalDateTime banDate;

  @Size(max = 255)
  @Column(name = "ban_reason")
  private String banReason;

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
