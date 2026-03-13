package dev.barrikeit.security.service;

import dev.barrikeit.security.model.domain.UserSession;
import dev.barrikeit.security.model.repository.UserSessionRepository;
import dev.barrikeit.security.util.TokenType;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.exceptions.NotFoundException;
import java.time.LocalDateTime;
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
      UUID userCode,
      String jti,
      String jtiPair,
      LocalDateTime issuedAt,
      LocalDateTime expiresAt,
      String tokenType) {

    repository.save(
        UserSession.builder()
            .userCode(userCode)
            .jti(jti)
            .jtiPair(jtiPair)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .tokenType(tokenType)
            .build());
  }

  public List<UserSession> findUserSessions(UUID userCode) {
    return repository.findAllByUserCode(userCode);
  }

  public UserSession findSession(UUID userCode, String jti) {
    return repository
        .findByUserCodeAndJti(userCode, jti)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, jti));
  }

  public boolean validateToken(UUID userCode, String jti) {
    return repository.existsByUserCodeAndJti(userCode, jti);
  }

  public long activeSessions(UUID userCode, TokenType tokenType) {
    return repository.countByUserCodeAndTokenType(userCode, tokenType.name());
  }

  @Transactional
  public void revokeTokenPair(UUID userCode, String jti) {
    UserSession session =
        repository
            .findByUserCodeAndJti(userCode, jti)
            .orElseThrow(() -> new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, jti));

    repository.deleteByUserCodeAndJti(userCode, session.getJti());
    repository.deleteByUserCodeAndJti(userCode, session.getJtiPair());
  }

  public void revokeAll(UUID userCode) {
    repository.deleteByUserCode(userCode);
  }
}
