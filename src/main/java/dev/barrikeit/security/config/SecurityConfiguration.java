package dev.barrikeit.security.config;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.security.filter.AppHeaderValidatorFilter;
import dev.barrikeit.security.filter.JwtFilter;
import dev.barrikeit.security.service.UserSessionService;
import dev.barrikeit.security.util.JwtUtil;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Main security configuration for the application.
 *
 * <p>This configuration is enabled when {@code application.security.enabled=true} or when the
 * property is missing (default secure-by-default behavior).
 *
 * <p>Responsibilities:
 *
 * <ul>
 *   <li>JWT-based stateless authentication
 *   <li>CORS and CSRF configuration
 *   <li>HTTP security headers
 *   <li>Authorization rules per endpoint
 *   <li>Custom exception handling
 * </ul>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
@Import(SecurityExceptionHandler.class)
public class SecurityConfiguration {

  private final ApplicationProperties.ServerProperties serverProperties;
  private final SecurityProperties securityProperties;
  private final SecurityExceptionHandler exceptionHandler;
  private final JwtUtil jwtUtil;
  private final UserSessionService userSessionService;

  /**
   * Defines the main {@link SecurityFilterChain} for the application.
   *
   * <p>This filter chain:
   *
   * <ul>
   *   <li>Applies CORS and CSRF protections
   *   <li>Uses stateless session management (JWT)
   *   <li>Registers JWT and header validation filters
   *   <li>Defines authorization rules for endpoints
   * </ul>
   *
   * @param http the {@link HttpSecurity} to configure
   * @return the configured security filter chain
   * @throws Exception if configuration fails
   */
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.warn("Security Configuration active");
    String apiPath = serverProperties.getServlet().getApiPath();
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .headers(
            headers ->
                headers
                    .frameOptions(options -> options.sameOrigin())
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                    .addHeaderWriter(
                        new StaticHeadersWriter("Referrer-Policy", "strict-origin-when-cross-origin"))
                    .addHeaderWriter(
                        new StaticHeadersWriter("Permissions-Policy", "camera=(), microphone=(), geolocation=()"))
                    .addHeaderWriter(
                        new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin")))
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(exceptionHandler)
                    .accessDeniedHandler(exceptionHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        apiPath + "/public/**",
                        apiPath + "/error/**",
                        apiPath + "/error",
                        apiPath + "/version/**",
                        apiPath + "/version",
                        apiPath + "/auth/**")
                    .permitAll()
                    .requestMatchers(apiPath + "/watchdog/**")
                    .hasAuthority("WTC")
                    .requestMatchers(apiPath + "/users/**", apiPath + "/roles/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults());

    http.addFilterBefore(appHeaderValidatorFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(jwtFilter(), AppHeaderValidatorFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    SecurityProperties.CorsProperties cors = securityProperties.getCors();

    if (Boolean.TRUE.equals(cors.getEnabled())) {
      configuration.setAllowedOriginPatterns(splitTrimmed(cors.getAllowed().getOrigins()));
      configuration.setAllowedMethods(splitTrimmed(cors.getAllowed().getMethods()));
      configuration.setAllowedHeaders(splitTrimmed(cors.getAllowed().getHeaders()));
      configuration.setAllowCredentials(true);
    }

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(cors.getPath().getPattern(), configuration);
    return source;
  }

  @Bean
  public JwtFilter jwtFilter() {
    return new JwtFilter(jwtUtil, userSessionService);
  }

  @Bean
  public AppHeaderValidatorFilter appHeaderValidatorFilter() {
    return new AppHeaderValidatorFilter(
        serverProperties.getServlet().getApiPath(), securityProperties.getAppValidatorFilter());
  }

  private static List<String> splitTrimmed(String csv) {
    return Arrays.stream(csv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }
}
