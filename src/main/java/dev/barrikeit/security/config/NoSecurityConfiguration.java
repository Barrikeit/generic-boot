package dev.barrikeit.security.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration that completely disables application security.
 *
 * <p>This configuration is conditionally enabled when the property {@code
 * app.security.enabled=false}.
 *
 * <p>When active:
 *
 * <ul>
 *   <li>All incoming HTTP requests are allowed without authentication
 *   <li>CSRF protection is disabled
 *   <li>No JWT, session, or authorization filters are applied
 * </ul>
 *
 * <p>This is intended for:
 *
 * <ul>
 *   <li>Local development
 *   <li>Integration testing
 *   <li>Deployments where security is handled externally (API Gateway, VPN, etc.)
 * </ul>
 */
@Log4j2
@Configuration
@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
public class NoSecurityConfiguration {

  /**
   * Defines a {@link SecurityFilterChain} that permits all requests.
   *
   * <p>This filter chain:
   *
   * <ul>
   *   <li>Disabled CSRF since no authentication state is maintained
   *   <li>Allows any request without authentication
   * </ul>
   *
   * @param http the {@link HttpSecurity} to configure
   * @return a security filter chain with no authentication or authorization
   * @throws Exception if the security configuration fails
   */
  @Bean
  SecurityFilterChain noSecurity(HttpSecurity http) throws Exception {
    log.warn("Security Configuration NOT active");
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
}
