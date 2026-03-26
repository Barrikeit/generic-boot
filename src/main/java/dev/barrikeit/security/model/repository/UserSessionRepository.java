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

  List<UserSession> findAllByUserId(UUID userId);

  Optional<UserSession> findByUserIdAndJti(UUID userId, String jti);

  boolean existsByUserIdAndJti(UUID userId, String jti);

  int countByUserIdAndTokenType(UUID userId, String tokenType);

  void deleteByUserId(UUID userId);

  void deleteByUserIdAndJti(UUID userId, String jti);

  @Transactional
  void deleteByExpiresAtBefore(LocalDateTime now);
}
