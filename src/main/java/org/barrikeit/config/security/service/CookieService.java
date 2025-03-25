package org.barrikeit.config.security.service;

import jakarta.servlet.http.HttpServletResponse;
import org.barrikeit.util.constants.JwtConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

  @Value("${auth.cookie.http.only}")
  private boolean cookieHttpOnly;

  @Value("${auth.cookie.secure}")
  private boolean cookieSecure;

  @Value("${auth.cookie.path}")
  private String cookiePath;

  @Value("${auth.cookie.domain}")
  private String cookieDomain;

  @Value("${auth.cookie.max.age}")
  private Integer cookieMaxAge;

  @Value("${auth.cookie.samesite}")
  private String cookieSamesite;

  public void deleteJwtCookie(HttpServletResponse response) {
    // Construir la cookie con ResponseCookie para eliminarla
    ResponseCookie cookie =
        ResponseCookie.from(JwtConstants.JWT_COOKIE_NAME, "")
            .httpOnly(cookieHttpOnly)
            .secure(cookieSecure) // Establecer en true si HTTPS
            .path(cookiePath)
            .domain(cookieDomain)
            .maxAge(0) // Establecer maxAge a 0 para eliminarla
            // .sameSite(cookieSamesite) // SameSite puede ser "Lax", "Strict", etc.
            .build();

    // Agregar la cookie al encabezado de la respuesta para eliminarla
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  public ResponseCookie createJwtCookie(String encodedJwt) {
    // Construir la cookie usando ResponseCookie
    return ResponseCookie.from(JwtConstants.JWT_COOKIE_NAME, encodedJwt)
        .httpOnly(cookieHttpOnly)
        .secure(cookieSecure) // Establecer en true si HTTPS
        .path(cookiePath)
        .domain(cookieDomain)
        .maxAge(cookieMaxAge) // Duración de la cookie
        .sameSite(cookieSamesite) // Agregar SameSite como "Lax"
        .build();
  }
}
