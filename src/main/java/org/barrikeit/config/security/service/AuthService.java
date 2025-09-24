package org.barrikeit.config.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.barrikeit.config.security.model.domain.BasicUserDetails;
import org.barrikeit.config.security.model.domain.Jwt;
import org.barrikeit.config.security.model.domain.JwtAuth;
import org.barrikeit.config.security.model.repository.AuthRepository;
import org.barrikeit.config.security.rest.dto.JwtDto;
import org.barrikeit.config.security.rest.dto.LoginDto;
import org.barrikeit.config.security.rest.dto.RegisterDto;
import org.barrikeit.config.security.util.JwtDecoder;
import org.barrikeit.config.security.util.JwtProvider;
import org.barrikeit.model.domain.User;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.barrikeit.util.exceptions.UnExpectedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService<S extends Session> {

  private final AuthRepository authRepository;

  private final BasicUserDetailsService basicUserDetailsService;
  private final SessionService<S> sessionService;
  private final ObjectMapper objectMapper;
  private final JwtProvider jwtProvider;
  private final JwtDecoder jwtDecoder;

  @Value("${spring.security.http.session-management.concurrency-control.max-sessions}")
  private Integer maxNumberConcurrentSessionsUser;

  /**
   * Register method
   *
   * @param registerDto the data to register the user
   * @return RegisterDto of the registered user
   * @throws
   */
  public String register(RegisterDto registerDto) {
    return basicUserDetailsService.register(registerDto);
  }

  /**
   * Verify user method, the token is sent to the email of the user
   *
   * @param verificationToken the token to verify the user
   * @throws
   */
  public void verify(String verificationToken) {
    basicUserDetailsService.verify(verificationToken);
  }

  /**
   * Login method
   *
   * @param request the HTTP request containing the session
   * @param loginDto the data to login the user
   * @return JwtDto with the token
   * @throws
   */
  public JwtDto login(HttpServletRequest request, LoginDto loginDto) {
    validateSession(request, loginDto);

    request.getSession(false).invalidate();
    HttpSession httpSession = request.getSession(true);

    try {
      JwtDto jwtDto = signIn(loginDto, httpSession.getId());
      associateUserWithSession(loginDto, jwtDto, httpSession);
      return jwtDto;
    } catch (Exception e) {
      request.getSession(false).invalidate();
      throw e;
    }
  }

  private void validateSession(HttpServletRequest request, LoginDto loginDto) {
    if (request == null || request.getSession(true) == null) {
      throw new IllegalArgumentException(ExceptionConstants.ERROR_REQUEST_MUST_NOT_BE_NULL);
    }

    if (!sessionExists(request)) {
      Collection<? extends Session> usersSessions =
          sessionService.findByPrincipalName(loginDto.getUsername()).values();
      if (usersSessions.size() >= maxNumberConcurrentSessionsUser) {
        request.getSession(false).invalidate();
        throw new BadRequestException(
            ExceptionConstants.ERROR_MAX_SESSIONS_CONCURRENT_USER, loginDto.getUsername());
      }
    }
  }

  private boolean sessionExists(HttpServletRequest request) {
    String token = extractToken(request);
    String sessionId = "";
    S sesion;
    if (StringUtils.hasText(token)) {
      sessionId = jwtDecoder.getSessionIdFromToken(token);
    } else {
      sessionId = request.getSession(false).getId();
    }
    sesion = this.sessionService.findById(sessionId);
    return sesion != null
        && sesion.getAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME) != null;
  }

  private String extractToken(HttpServletRequest request) {
    String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
    String jwt = null;
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      jwt = headerAuth.substring(7);
    }
    return jwt;
  }

  private JwtDto signIn(LoginDto loginDto, String sessionId) {
    final String username = loginDto.getUsername();
    final String password = loginDto.getPassword();
    final Authentication authentication;
    final Jwt jwt;
    final Jwt jwtRefresh;
    User user;
    try {
      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(username, password);
      authentication = authRepository.authenticate(token);

      if (authentication == null) {
        throw new NotFoundException(ExceptionConstants.NOT_FOUND, username);
      }

      jwt = jwtProvider.generateToken((BasicUserDetails) authentication.getPrincipal(), sessionId);
      jwtRefresh =
          jwtProvider.generateRefreshToken(
              (BasicUserDetails) authentication.getPrincipal(), sessionId);
    } catch (AuthenticationException e) {
      user = basicUserDetailsService.findByUsername(username);
      basicUserDetailsService.checkAttempts(user);
      throw e;
    }
    return getJwtDto(jwt, jwtRefresh, username);
  }

  private JwtDto getJwtDto(Jwt newToken, Jwt newTokenRefresh, String username) {
    User user = basicUserDetailsService.findByUsername(username);
    if (user.isBanned())
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_BANNED, user.getUsername(), user.getBanDate());

    if (!user.isEnabled())
      throw new BadRequestException(ExceptionConstants.ERROR_USER_NOT_ENABLED, user.getUsername());

    basicUserDetailsService.updateLoginDateAndResetAttempts(user);
    return createJwtDto(newToken, newTokenRefresh);
  }

  private JwtDto createJwtDto(Jwt token, Jwt refreshToken) {
    return JwtDto.builder()
        .expireAt(token.getExpiresAt())
        .jwt(token.getJwtCache(false))
        .refreshToken(refreshToken.getJwtCache(true))
        .expireRefreshAt(refreshToken.getExpiresAt())
        .userDto(basicUserDetailsService.findDtoByUsername(token.getSubject()))
        .build();
  }

  private void associateUserWithSession(LoginDto loginDto, JwtDto jwtDto, HttpSession httpSession) {
    JwtAuth auth =
        new JwtAuth(
            loginDto.getUsername(),
            null,
            jwtDto.getJwt(),
            jwtDecoder.getAuthorities(jwtDto.getJwt()));
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);

    httpSession.setAttribute(
        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, loginDto.getUsername());
    httpSession.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());
  }

  /**
   * Refresh JWT method
   *
   * @param request the HTTP request containing the session
   * @return JwtDto with the refreshed token
   * @throws
   */
  public JwtDto refresh(HttpServletRequest request) {
    String token = extractToken(request);
    BasicUserDetails userDetails = loadUserDetails(token);

    request.getSession(false).invalidate();
    HttpSession session = request.getSession(true);
    String sessionId = session.getId();

    JwtDto jwtDto = refreshToken(userDetails, sessionId);
    String username = userDetails.getUsername();
    JwtAuth authentication =
        new JwtAuth(
            username, null, jwtDto.getJwt(), this.jwtDecoder.getAuthorities(jwtDto.getJwt()));
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext());

    session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, username);
    return jwtDto;
  }

  private BasicUserDetails loadUserDetails(String jwt) throws AuthenticationException {
    if (!StringUtils.hasText(jwt)) {
      throw new PreAuthenticatedCredentialsNotFoundException(
          ExceptionConstants.ERROR_TOKEN_NOT_PRESENT);
    }

    DecodedJWT decodedJWT;
    try {
      decodedJWT = jwtDecoder.validateAndRetrieveDecodedToken(jwt);
      boolean isRefresh = jwtDecoder.isRefresh(decodedJWT);
      if (!isRefresh) {
        throw new BadRequestException(ExceptionConstants.ERROR_TOKEN_INVALID);
      }
    } catch (TokenExpiredException ex) {
      throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_EXPIRED);
    } catch (JWTVerificationException e) {
      throw new BadCredentialsException(ExceptionConstants.ERROR_TOKEN_INVALID);
    }

    String username = decodedJWT.getSubject();
    return (BasicUserDetails) basicUserDetailsService.loadUser(username);
  }

  public JwtDto refreshToken(BasicUserDetails userDetails, String sessionId) {
    Jwt newToken = jwtProvider.generateToken(userDetails, sessionId);
    Jwt newTokenRefresh = jwtProvider.generateRefreshToken(userDetails, sessionId);

    return getJwtDto(newToken, newTokenRefresh, userDetails.getUsername());
  }

  /**
   * Logout method, invalidate the session
   *
   * @param request the HTTP request containing the session
   * @throws
   */
  public void logout(HttpServletRequest request) {
    request.getSession().invalidate();
  }

  /**
   * Check Login through cookie method
   *
   * @param cookie
   * @return JwtDto with the refreshed token
   * @throws
   */
  public JwtDto checkLogin(String cookie) {
    if (cookie == null || cookie.trim().isEmpty()) {
      throw new BadRequestException(ExceptionConstants.EMPTY_COOKIE);
    }

    try {
      String decodedJson = new String(Base64.getDecoder().decode(cookie));
      return objectMapper.readValue(decodedJson, JwtDto.class);
    } catch (JsonProcessingException e) {
      throw new UnExpectedException(ExceptionConstants.DESERIALIZED_COOKIE, e);
    }
  }
}
