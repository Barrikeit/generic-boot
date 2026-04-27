package dev.barrikeit.security.config.filter;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.security.config.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@AllArgsConstructor
public class AppHeaderValidatorFilter extends OncePerRequestFilter {

  private final ApplicationProperties.ServerProperties.ServletProperties servletProperties;
  private final SecurityProperties.AppValidatorFilterProperties appValidatorFilterProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    String requestURI = request.getRequestURI();
    String contextPath = request.getContextPath();
    String apiPath = servletProperties.getApiPath();
    String endpoint = requestURI.substring(contextPath.length() + apiPath.length());

    log.debug("requestURI={} | contextPath={} | apiPath={}", requestURI, contextPath, apiPath);

    if (isHeaderFreeEndpoint(endpoint)) {
      log.debug("Peticion al endpoint {} PERMITIDA (endpoint publico)", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    if (Boolean.TRUE.equals(appValidatorFilterProperties.getAppHeaderNameValidationFilter())) {
      String calledAppId = request.getHeader(appValidatorFilterProperties.getAppHeaderName());
      if (calledAppId == null
          || calledAppId.isBlank()
          || !calledAppId.equals(appValidatorFilterProperties.getAppSelfName())) {
        log.debug("Peticion al modulo {} al endpoint {} RECHAZADA", calledAppId, requestURI);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      log.debug("Peticion al modulo {} al endpoint {} ACEPTADA", calledAppId, requestURI);
    }

    filterChain.doFilter(request, response);
  }

  private boolean isHeaderFreeEndpoint(String endpoint) {
    return endpoint.startsWith("/public")
        || endpoint.startsWith("/error")
        || endpoint.startsWith("/version");
  }
}
