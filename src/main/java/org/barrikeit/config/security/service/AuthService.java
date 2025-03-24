package org.barrikeit.config.security.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.barrikeit.config.security.model.domain.BasicUserDetails;
import org.barrikeit.config.security.model.domain.Jwt;
import org.barrikeit.config.security.model.domain.JwtAuth;
import org.barrikeit.config.security.model.repository.AuthRepository;
import org.barrikeit.config.security.service.dto.JwtDto;
import org.barrikeit.config.security.service.dto.LoginDto;
import org.barrikeit.config.security.util.JwtDecoder;
import org.barrikeit.config.security.util.JwtProvider;
import org.barrikeit.model.domain.User;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.exceptions.NotFoundException;
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

  private final JwtDecoder jwtDecoder;
  private final JwtProvider jwtProvider;
  private final SessionService<S> sessionService;
  private final BasicUserDetailsService basicUserDetailsService;

  @Value("${spring.security.http.session-management.concurrency-control.max-sessions}")
  private Integer maxNumberConcurrentSessionsUser;

  public JwtDto login(LoginDto loginDto, HttpServletRequest request) {
    if (!sessionExists(request)) {
      Collection<? extends Session> usersSessions =
          sessionService.findByPrincipalName(loginDto.getUsername()).values();
      if (usersSessions.size() >= maxNumberConcurrentSessionsUser) {
        if (request.getSession(false) != null) {
          request.getSession(false).invalidate();
        }
        throw new BadRequestException(
            ExceptionConstants.ERROR_MAX_SESSIONS_CONCURRENT_USER, loginDto.getUsername());
      }
    }

    if (request.getSession(false) != null) {
      request.getSession(false).invalidate();
    }
    HttpSession httpSession = request.getSession(true);
    String sessionId = httpSession.getId();

    try {
      JwtDto dto = signIn(loginDto, sessionId);
      JwtAuth auth =
          new JwtAuth(
              loginDto.getUsername(), null, dto.getJwt(), jwtDecoder.getAuthorities(dto.getJwt()));
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);

      httpSession.setAttribute(
          FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, loginDto.getUsername());
      httpSession.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          SecurityContextHolder.getContext());
      return dto;
    } catch (Exception e) {
      request.getSession(false).invalidate();
      throw e;
    }
  }

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

  private boolean sessionExists(HttpServletRequest request) {
    String token = extractToken(request);
    String sessionId = "";
    S sesion;
    if (StringUtils.hasText(token)) {
      sessionId = jwtDecoder.getSessionIdFromToken(token);
    } else {
      if (request.getSession(false) != null) {
        sessionId = request.getSession(false).getId();
      }
    }
    sesion = this.sessionService.findById(sessionId);
    return sesion != null
        && sesion.getAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME) != null;
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
        throw new NotFoundException(ExceptionConstants.ERROR_NOT_FOUND, username);
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

  private String extractToken(HttpServletRequest request) {
    String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
    String jwt = null;
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      jwt = headerAuth.substring(7);
    }
    return jwt;
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

  private JwtDto getJwtDto(Jwt newToken, Jwt newTokenRefresh, String username) {
    User user = basicUserDetailsService.findByUsername(username);
    if (Boolean.TRUE.equals(user.isBanned()))
      throw new BadRequestException(
          ExceptionConstants.ERROR_USER_BANNED, user.getUsername(), user.getBanDate());

    if (Boolean.FALSE.equals(user.isEnabled()))
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
}
