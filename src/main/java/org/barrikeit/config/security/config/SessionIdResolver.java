package org.barrikeit.config.security.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.barrikeit.config.security.util.JwtDecoder;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.util.StringUtils;

public class SessionIdResolver extends HeaderHttpSessionIdResolver {

  private final JwtDecoder jwtDecoder;

  public SessionIdResolver(JwtDecoder jwtDecoder) {
    super("Authorization");
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  public List<String> resolveSessionIds(HttpServletRequest request) {
    // Logica para extraer SessionId del request header
    String jwt = extractToken(request);
    if (jwt != null && !jwt.equalsIgnoreCase("null")) {
      return List.of(jwtDecoder.getSessionIdFromToken(jwt));
    } else {
      return super.resolveSessionIds(request);
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
}
// TODO esto se puede eliminar?
