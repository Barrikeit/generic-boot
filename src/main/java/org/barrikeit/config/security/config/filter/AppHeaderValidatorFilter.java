package org.barrikeit.config.security.config.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.config.SecurityProperties;

@Log4j2
@AllArgsConstructor
public class AppHeaderValidatorFilter implements Filter {

  private final SecurityProperties.AppValidatorFilterProperties appValidatorFilterProperties;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    var httpRequest = (HttpServletRequest) request;
    var httpResponse = (HttpServletResponse) response;

    // Ignorar el filtro si la URL apunta a un endpoint público o a un error
    String path = httpRequest.getRequestURI();
    if (path != null && (path.contains("/public/") || path.contains("/error"))) {
      filterChain.doFilter(request, response);
      return;
    }

    // Validar cabecera personalizada si el filtro está habilitado en configuración
    if (Boolean.TRUE.equals(appValidatorFilterProperties.getAppHeaderNameValidationFilter())) {
      // Obtener el valor del header definido en la configuración
      String calledAppId = httpRequest.getHeader(appValidatorFilterProperties.getAppHeaderName());
      // Rechazar la petición si el header es nulo, vacío o no coincide con el nombre esperado
      if (calledAppId == null
          || calledAppId.isBlank()
          || !calledAppId.equals(appValidatorFilterProperties.getAppSelfName())) {
        log.debug("Peticion a {}{} RECHAZADA", calledAppId, path);
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }

      log.debug("Peticion a {}{} ACEPTADA", calledAppId, path);
    }

    filterChain.doFilter(request, response);
  }
}
