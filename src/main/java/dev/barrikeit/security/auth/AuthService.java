package dev.barrikeit.security.auth;

import dev.barrikeit.security.config.SecurityProperties;
import dev.barrikeit.security.rest.dto.JwtDto;
import dev.barrikeit.security.rest.dto.RegisterDto;
import dev.barrikeit.security.service.UserSessionService;
import dev.barrikeit.security.util.JwtUtil;
import dev.barrikeit.service.dto.RoleDto;
import dev.barrikeit.service.dto.UserDto;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

/**
 * Application authentication service.
 *
 * <p>Extends the core {@link dev.barrikeit.security.service.AuthService} — {@code login}, {@code
 * logout} and {@code refresh} are inherited unchanged (the brute-force lockout lives in {@link
 * BasicUserDetailsService#authenticate}). Only the application-specific behaviour is added here:
 * registration with email + verification token, email verification, and session check.
 *
 * <p>Bean name {@code authService} suppresses core's auto-configured default via {@code
 * @ConditionalOnMissingBean(name="authService")}.
 */
@Service
public class AuthService extends dev.barrikeit.security.service.AuthService {

  private final BasicUserDetailsService basicUserDetailsService;
  private final UserSessionService sessionService;
  private final JwtUtil jwtUtil;

  public AuthService(
      SecurityProperties securityProperties,
      BasicUserDetailsService basicUserDetailsService,
      UserSessionService sessionService,
      JwtUtil jwtUtil) {
    super(securityProperties, basicUserDetailsService, sessionService, jwtUtil);
    this.basicUserDetailsService = basicUserDetailsService;
    this.sessionService = sessionService;
    this.jwtUtil = jwtUtil;
  }

  /** Registers a new user with the default role, returning a URL-safe verification token. */
  @Override
  public String register(RegisterDto registerDto) {
    UserDto userDto =
        UserDto.builder()
            .username(registerDto.getUsername())
            .email(registerDto.getEmail())
            .password(registerDto.getPassword())
            .roles(Set.of(RoleDto.builder().code("US").build()))
            .build();
    return basicUserDetailsService.register(userDto);
  }

  /** Verifies and activates a user account using a verification token. */
  public void verify(String verificationToken) {
    basicUserDetailsService.verify(verificationToken);
  }

  /** Validates that an access token's session is still active (not revoked). */
  public JwtDto checkSession(String accessToken) {
    UUID userId = jwtUtil.extractUserId(accessToken);
    String jti = jwtUtil.extractJti(accessToken);
    if (!sessionService.validateToken(userId, jti)) {
      throw new SessionAuthenticationException("Sesión no válida");
    }
    return JwtDto.builder()
        .token(accessToken)
        .expireAt(jwtUtil.extractExpirationDate(accessToken))
        .build();
  }
}
