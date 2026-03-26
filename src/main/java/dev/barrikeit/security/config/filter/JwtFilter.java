package dev.barrikeit.security.config.filter;

import dev.barrikeit.security.service.UserSessionService;
import dev.barrikeit.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserSessionService userSessionService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String jwt = extractToken(request);

    if (!StringUtils.hasText(jwt)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String username = jwtUtil.extractUsername(jwt);
      validateActiveSession(jwt);

      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(username, null, jwtUtil.extractAuthorities(jwt));
      auth.setDetails(jwt);
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);

      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token expirado");
    } catch (SignatureException e) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
    } catch (SessionAuthenticationException e) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
    } catch (Exception e) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
    }
  }

  private void validateActiveSession(String jwt) {
    UUID userId = jwtUtil.extractUserId(jwt);
    String jti = jwtUtil.extractJti(jwt);
    if (!userSessionService.validateToken(userId, jti)) {
      throw new SessionAuthenticationException("Sesión no válida");
    }
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    return (StringUtils.hasText(header) && header.startsWith("Bearer "))
        ? header.substring(7)
        : null;
  }
}
