package org.barrikeit.config.security.model.domain;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuth extends UsernamePasswordAuthenticationToken {

  private final String jwt;

  public JwtAuth(Object principal, Object credentials) {
    this(principal, credentials, null);
  }

  public JwtAuth(Object principal, Object credentials, String jwt) {
    super(principal, credentials);
    this.jwt = jwt;
  }

  public JwtAuth(
      Object principal,
      Object credentials,
      String jwt,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.jwt = jwt;
  }

  public String getJwt() {
    return this.jwt;
  }
}
