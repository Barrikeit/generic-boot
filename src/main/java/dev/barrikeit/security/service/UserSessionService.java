package dev.barrikeit.security.service;

import dev.barrikeit.security.model.domain.UserSession;
import dev.barrikeit.security.model.repository.UserSessionRepository;
import dev.barrikeit.security.util.TokenType;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.exceptions.NotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserSessionService {

  private final UserSessionRepository repository;

  public void createSession(
      UUID userId,
      String jti,
      String jtiPair,
      OffsetDateTime issuedAt,
      OffsetDateTime expiresAt,
      String tokenType) {
    repository.save(
        UserSession.builder()
            .userId(userId)
            .jti(jti)
            .jtiPair(jtiPair)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .tokenType(tokenType)
            .build());
  }

  public List<UserSession> findUserSessions(UUID userId) {
    return repository.findAllByUserId(userId);
  }

  public UserSession findSession(UUID userId, String jti) {
    return repository
        .findByUserIdAndJti(userId, jti)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, jti));
  }

  public boolean validateToken(UUID userId, String jti) {
    return repository.existsByUserIdAndJti(userId, jti);
  }

  public long activeSessions(UUID userId, TokenType tokenType) {
    return repository.countByUserIdAndTokenType(userId, tokenType.name());
  }

  @Transactional
  public void revokeTokenPair(UUID userId, String jti) {
    UserSession session =
        repository
            .findByUserIdAndJti(userId, jti)
            .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, jti));

    repository.deleteByUserIdAndJti(userId, session.getJti());
    repository.deleteByUserIdAndJti(userId, session.getJtiPair());
  }

  public void revokeAll(UUID userId) {
    repository.deleteByUserId(userId);
  }
}
