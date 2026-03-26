package dev.barrikeit.security.model.domain;

import dev.barrikeit.model.domain.base.GenericEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = EntityConstants.USER_SESSIONS)
public class UserSession extends GenericEntity<UUID> {

  @Column(name = EntityConstants.ID_USER, nullable = false, updatable = false)
  private UUID userId;

  @JdbcTypeCode(Types.CHAR)
  @Column(
      name = EntityConstants.JTI,
      nullable = false,
      columnDefinition = EntityConstants.BPCHAR_COLUMN_DEFINITION)
  private String jti;

  @JdbcTypeCode(Types.CHAR)
  @Column(
      name = EntityConstants.JTI_PAIR,
      nullable = false,
      columnDefinition = EntityConstants.BPCHAR_COLUMN_DEFINITION)
  private String jtiPair;

  @Column(
      name = EntityConstants.ISSUED_AT,
      nullable = false,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private OffsetDateTime issuedAt;

  @Column(
      name = EntityConstants.EXPIRES_AT,
      nullable = false,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private OffsetDateTime expiresAt;

  @Column(name = EntityConstants.TOKEN_TYPE, nullable = false)
  private String tokenType; // ACCESS / REFRESH

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserSession that)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(id, that.id)
        && Objects.equals(jti, that.jti)
        && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
