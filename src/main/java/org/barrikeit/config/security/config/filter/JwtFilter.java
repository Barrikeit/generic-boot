package org.barrikeit.config.security.config.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.model.domain.JwtAuth;
import org.barrikeit.config.security.service.SessionService;
import org.barrikeit.config.security.util.JwtDecoder;
import org.barrikeit.config.security.util.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;
  private final JwtDecoder jwtDecoder;
  private final SessionService sessionService;

  @Value("${spring.mvc.servlet.path}")
  private String servletPath;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = extractToken(request);

    if (!StringUtils.hasText(jwt)
        || request.getRequestURI().equals(servletPath + "/auth/register")
        || request.getRequestURI().equals(servletPath + "/auth/verify")
        || request.getRequestURI().equals(servletPath + "/auth/login")
        || request.getRequestURI().equals(servletPath + "/auth/refresh")
        || request.getRequestURI().equals(servletPath + "/auth/logout")) {
      filterChain.doFilter(request, response);
    } else {
      try {
        String username = this.jwtProvider.validateTokenAndRetrieveSubject(jwt);
        // recuperar sessionId del token para validar que pertenece a una sesion activa
        validateActiveSessionToken(jwt);

        JwtAuth authentication =
            new JwtAuth(username, null, jwt, this.jwtDecoder.getAuthorities(jwt));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        filterChain.doFilter(request, response);
      } catch (TokenExpiredException e) {
        SecurityContextHolder.clearContext();
        log.warn(e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token expirado");
      } catch (JWTVerificationException e) {
        SecurityContextHolder.clearContext();
        log.warn(e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error al verificar el Token");
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
        log.error(e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el Servidor");
      }
    }
  }

  private String extractToken(HttpServletRequest request) {
    String headerAuth = request.getHeader("Authorization");
    String jwt = null;
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      jwt = headerAuth.substring(7);
    }
    return jwt;
  }

  private void validateActiveSessionToken(String jwt) {
    // recuperar sessionId del token para validar que pertenece a una sesion activa
    String sessionId = "";
    DecodedJWT decodedJWT = this.jwtDecoder.validateAndRetrieveDecodedToken(jwt);
    sessionId = this.jwtDecoder.getSessionIdClaim(decodedJWT);
    // lanzar Excepcion si no existe la Sesion en la tabla de Sesiones
    boolean existeSesionToken = sessionService.existsSession(sessionId);
    if (!existeSesionToken) {
      throw new TokenExpiredException(
          "Token expirado por sesion", decodedJWT.getExpiresAtAsInstant());
    }
  }
}
