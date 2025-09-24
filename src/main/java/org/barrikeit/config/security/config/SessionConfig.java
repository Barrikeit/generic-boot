package org.barrikeit.config.security.config;

import lombok.RequiredArgsConstructor;
import org.barrikeit.config.security.util.JwtDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@RequiredArgsConstructor
public class SessionConfig {

  private final JwtDecoder jwtDecoder;

  @Bean
  public HttpSessionIdResolver httpSessionIdResolver() {
    return new SessionIdResolver(jwtDecoder);
  }
}
// TODO esto se puede eliminar?
