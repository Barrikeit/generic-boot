package dev.barrikeit.security.service;

import dev.barrikeit.security.config.SecurityProperties;
import dev.barrikeit.security.model.domain.BasicUserDetails;
import dev.barrikeit.security.model.domain.UserSession;
import dev.barrikeit.security.rest.dto.JwtDto;
import dev.barrikeit.security.rest.dto.LoginDto;
import dev.barrikeit.security.rest.dto.RegisterDto;
import dev.barrikeit.security.util.JwtUtil;
import dev.barrikeit.security.util.TokenType;
import dev.barrikeit.service.dto.RoleDto;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.util.TimeUtil;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.util.exceptions.BadRequestException;
import dev.barrikeit.util.exceptions.NotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

  private final SecurityProperties securityProperties;
  private final BasicUserDetailsService basicUserDetailsService;
  private final UserSessionService sessionService;
  private final JwtUtil jwtUtil;

  /**
   * Registers a new user in the system.
   *
   * <p>Creates a user with default roles, persists cryptographic public keys, encodes the password,
   * and generates a verification token.
   *
   * @param registerDto DTO containing user registration data
   * @return URL-safe verification token used to activate the account
   * @throws BadRequestException if username or email already exists
   */
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

  /**
   * Verifies and activates a user account using a verification token.
   *
   * <p>Once verified, the user account is enabled and the token is invalidated.
   *
   * @param verificationToken verification token sent to the user
   * @throws NotFoundException if the token does not exist
   * @throws BadRequestException if the user is already verified
   */
  public void verify(String verificationToken) {
    basicUserDetailsService.verify(verificationToken);
  }

  /**
   * Authenticates a user and issues a new access and refresh token pair.
   *
   * <p>Enforces maximum concurrent access sessions and persists token sessions for tracking and
   * revocation purposes.
   *
   * @param loginDto DTO containing username and password
   * @return JWT DTO containing access and refresh tokens and expiration dates
   * @throws BadCredentialsException if credentials are invalid
   * @throws BadRequestException if maximum concurrent sessions is exceeded
   */
  public JwtDto login(LoginDto loginDto) {
    Authentication authentication =
        basicUserDetailsService.authenticate(
            UserDto.builder()
                .username(loginDto.getUsername())
                .password(loginDto.getPassword())
                .build());

    BasicUserDetails user = (BasicUserDetails) authentication.getPrincipal();

    return issueTokenPair(user);
  }

  /**
   * Refreshes access and refresh tokens using a valid refresh token.
   *
   * <p>Validates the refresh token, verifies its session in persistence, revokes the old refresh
   * token, and issues a new token pair.
   *
   * @param accessToken access JWT
   * @param refreshToken refresh JWT
   * @return newly issued access and refresh tokens
   * @throws BadCredentialsException if the token is invalid, expired, or revoked
   */
  public JwtDto refresh(String accessToken, String refreshToken) {
    try {
      if (!jwtUtil.isRefreshableToken(refreshToken)) {
        throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_INVALID);
      }
    } catch (ExpiredJwtException e) {
      throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_EXPIRED);
    }

    UUID refreshuserId = jwtUtil.extractUserId(refreshToken);
    String refreshJti = jwtUtil.extractJti(refreshToken);
    UserSession refreshSession = sessionService.findSession(refreshuserId, refreshJti);

    UUID accessuserId = jwtUtil.extractUserId(accessToken);
    String accessJti = jwtUtil.extractJti(accessToken);
    if (!refreshSession.getUserId().equals(accessuserId)) {
      throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_INVALID);
    }
    if (!refreshSession.getJtiPair().equals(accessJti)) {
      throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_INVALID);
    }

    BasicUserDetails user = basicUserDetailsService.loadUserByCode(accessuserId);
    sessionService.revokeTokenPair(accessuserId, accessJti);

    return issueTokenPair(user);
  }

  private JwtDto issueTokenPair(BasicUserDetails user) {
    UUID userId = user.getId();

    long activeSessions = sessionService.activeSessions(userId, TokenType.ACCESS);
    if (activeSessions >= securityProperties.getMaxConcurrentSessions()) {
      throw new BadRequestException(
          ExceptionConstants.ERROR_MAX_SESSIONS_CONCURRENT_USER, user.getUsername());
    }

    String accessJti = UUID.randomUUID().toString();
    String refreshJti = UUID.randomUUID().toString();

    String accessToken = jwtUtil.generateAccessToken(user, accessJti);
    String refreshToken = jwtUtil.generateRefreshToken(user, refreshJti);

    OffsetDateTime now = TimeUtil.offsetDateTimeNow();

    sessionService.createSession(
        userId,
        accessJti,
        refreshJti,
        now,
        TimeUtil.toOffsetDateTime(jwtUtil.extractExpirationDate(accessToken)),
        TokenType.ACCESS.name());

    sessionService.createSession(
        userId,
        refreshJti,
        accessJti,
        now,
        TimeUtil.toOffsetDateTime(jwtUtil.extractExpirationDate(refreshToken)),
        TokenType.REFRESH.name());

    return JwtDto.builder()
        .token(accessToken)
        .refreshToken(refreshToken)
        .expireAt(jwtUtil.extractExpirationDate(accessToken))
        .expireRefreshAt(jwtUtil.extractExpirationDate(refreshToken))
        .build();
  }

  /**
   * Logs out a user by revoking the access token session.
   *
   * <p>The token is invalidated in persistence, preventing further usage.
   *
   * @param accessToken active access JWT
   * @throws BadRequestException if the token is missing or invalid
   */
  public void logout(String accessToken) {
    UUID userId = jwtUtil.extractUserId(accessToken);
    String jti = jwtUtil.extractJti(accessToken);
    sessionService.revokeTokenPair(userId, jti);
  }

  /**
   * Checks if an access token session is valid.
   *
   * <p>Validates the token's existence in persistence to ensure it has not been revoked.
   *
   * @param accessToken active access JWT
   * @return JWT DTO if the session is valid
   * @throws BadRequestException if the token is missing or invalid
   */
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
