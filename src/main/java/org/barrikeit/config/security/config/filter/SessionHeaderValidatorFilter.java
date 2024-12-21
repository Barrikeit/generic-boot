package org.barrikeit.config.security.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.util.AuditHolder;
import org.barrikeit.config.security.model.domain.AuditInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
public class SessionHeaderValidatorFilter extends OncePerRequestFilter {

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    if (path != null && (path.contains("/public/") || path.contains("/error"))) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!activeProfile.equals("test")) {
      String sessionInfo = request.getHeader("x-aei-session");
      List<String> sessionInfoList =
          Collections.list(new StringTokenizer(sessionInfo, "#")).stream()
              .map(String.class::cast)
              .toList();

      AuditInfo auditInfo =
          new AuditInfo(applicationName, sessionInfoList.get(1), sessionInfoList.get(2));
      AuditHolder.setAuditInfo(auditInfo);
    }

    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}
