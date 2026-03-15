package dev.barrikeit.security.model.domain;

import dev.barrikeit.model.domain.base.BaseEntity;
import dev.barrikeit.util.constants.EntityConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.sql.Types;
import java.time.LocalDateTime;
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
public class UserSession extends BaseEntity {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = EntityConstants.ID_USER_SESSION, updatable = false, nullable = false)
  private UUID id;

  @Column(name = "code_user", nullable = false)
  private UUID userCode;

  @JdbcTypeCode(Types.CHAR)
  @Column(
      name = "jti",
      nullable = false,
      columnDefinition = EntityConstants.BPCHAR_COLUMN_DEFINITION)
  private String jti;

  @JdbcTypeCode(Types.CHAR)
  @Column(
      name = "jti_pair",
      nullable = false,
      columnDefinition = EntityConstants.BPCHAR_COLUMN_DEFINITION)
  private String jtiPair;

  @Column(
      name = "issued_at",
      nullable = false,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private LocalDateTime issuedAt;

  @Column(
      name = "expires_at",
      nullable = false,
      columnDefinition = EntityConstants.DATE_COLUMN_DEFINITION)
  private LocalDateTime expiresAt;

  @Column(name = "token_type", nullable = false)
  private String tokenType; // ACCESS / REFRESH

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserSession that)) return false;
    if (!super.equals(o)) return false;

    return Objects.equals(id, that.id)
        && Objects.equals(jti, that.jti)
        && Objects.equals(userCode, that.userCode);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
