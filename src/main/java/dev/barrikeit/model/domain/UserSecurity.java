package dev.barrikeit.model.domain;

import dev.barrikeit.springframework.data.entity.GenericMappedEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Objects;
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
@Table(name = EntityConstants.USER_SECURITY)
@AttributeOverride(
    name = EntityConstants.ID,
    column = @Column(name = EntityConstants.ID_USER, nullable = false, updatable = false))
@AssociationOverride(
    name = EntityConstants.MAPS_ID,
    joinColumns = @JoinColumn(name = EntityConstants.ID_USER, nullable = false, updatable = false))
public class UserSecurity extends GenericMappedEntity<UUID, User> {

  @Column(
      name = EntityConstants.REGISTRATION_DATE,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private OffsetDateTime registrationDate;

  @Column(name = EntityConstants.VERIFICATION_TOKEN, length = 20)
  private String verificationToken;

  @NotNull
  @Column(name = EntityConstants.ENABLED, nullable = false)
  private boolean enabled = false;

  @NotNull
  @Column(name = EntityConstants.LOGIN_ATTEMPTS, nullable = false)
  private Integer loginAttempts = 0;

  @Column(
      name = EntityConstants.LOGIN_DATE,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private OffsetDateTime loginDate;

  @NotNull
  @Column(name = EntityConstants.BANNED, nullable = false)
  private boolean banned = false;

  @Column(
      name = EntityConstants.BAN_DATE,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private OffsetDateTime banDate;

  @Column(name = EntityConstants.BAN_REASON)
  private String banReason;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserSecurity e)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(id, e.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "UserSecurity{" + "id=" + id + '}';
  }
}
