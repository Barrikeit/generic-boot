package org.barrikeit.config.security.service;

import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SessionService<S extends Session> {
  private final FindByIndexNameSessionRepository<S> repository;

  /**
   * Devuelve si existe un registro en la tabla de sesiones de Spring identificado por el valor
   * unico sessionId, es equivalente a devolver si existe una HttpSesion activa con ese sessionId
   *
   * @param sessionId Identificador de la Sesion
   * @return boolean indicando si existe un registro
   */
  public boolean existsSession(String sessionId) {
    return repository.findById(sessionId) != null;
  }

  public S findById(String sessionId) {
    return repository.findById(sessionId);
  }

  public Map<String, S> findByPrincipalName(String username) {
    return repository.findByPrincipalName(username);
  }
}
