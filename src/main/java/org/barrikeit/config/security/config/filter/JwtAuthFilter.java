package org.barrikeit.config.security.config.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.barrikeit.util.constants.ExceptionConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;

public class JwtAuthFilter extends AbstractPreAuthenticatedProcessingFilter {
  private boolean exceptionIfHeaderMissing = false;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    String principal = extractToken(request);
    if (principal == null && this.exceptionIfHeaderMissing) {
      throw new PreAuthenticatedCredentialsNotFoundException(
          ExceptionConstants.ERROR_TOKEN_NOT_PRESENT);
    }
    return principal;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  public void setExceptionIfHeaderMissing(boolean exceptionIfHeaderMissing) {
    this.exceptionIfHeaderMissing = exceptionIfHeaderMissing;
  }

  private String extractToken(HttpServletRequest request) {
    String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

    String jwt = null;
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      jwt = headerAuth.substring(7);
    }
    return jwt;
  }
}
