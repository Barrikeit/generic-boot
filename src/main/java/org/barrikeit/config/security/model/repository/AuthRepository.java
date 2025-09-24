package org.barrikeit.config.security.model.repository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository {
  Authentication authenticate(Authentication authentication) throws AuthenticationException;
}
