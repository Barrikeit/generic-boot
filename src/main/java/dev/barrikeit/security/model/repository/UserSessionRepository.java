package dev.barrikeit.security.model.repository;

import dev.barrikeit.model.repository.base.BaseRepository;
import dev.barrikeit.security.model.domain.UserSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserSessionRepository extends BaseRepository<UserSession, UUID> {

  List<UserSession> findAllByUserCode(UUID userCode);

  Optional<UserSession> findByUserCodeAndJti(UUID userCode, String jti);

  boolean existsByUserCodeAndJti(UUID userCode, String jti);

  int countByUserCodeAndTokenType(UUID userCode, String tokenType);

  void deleteByUserCode(UUID userCode);

  void deleteByUserCodeAndJti(UUID userCode, String jti);

  @Transactional
  void deleteByExpiresAtBefore(LocalDateTime now);
}
