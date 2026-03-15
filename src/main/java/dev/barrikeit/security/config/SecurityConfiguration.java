package dev.barrikeit.security.config;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.security.config.filter.AppHeaderValidatorFilter;
import dev.barrikeit.security.config.filter.JwtFilter;
import dev.barrikeit.security.service.UserSessionService;
import dev.barrikeit.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
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
@Log4j2
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
        .csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .requireCsrfProtectionMatcher(csrfRequestMatcher()))
        .headers(
            headers ->
                headers.frameOptions(
                    options ->
                        options
                            .sameOrigin()
                            .addHeaderWriter(
                                new StaticHeadersWriter(
                                    "X-Content-Security-Policy", "default-src 'self'"))
                            .addHeaderWriter(
                                new StaticHeadersWriter("X-WebKit-CSP", "default-src 'self'"))))
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
                    .hasRole("WATCHDOG")
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
  public RequestMatcher csrfRequestMatcher() {
    String contextPath = serverProperties.getServlet().getContextPath();
    String apiPath = serverProperties.getServlet().getApiPath();
    String fullPath = (contextPath.equals("/") ? "" : contextPath) + apiPath;

    return new RequestMatcher() {
      private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|POST|PUT|DELETE)$");
      private final RegexRequestMatcher apiMatcher =
          new RegexRequestMatcher("^" + fullPath + "/.*", null);

      @Override
      public boolean matches(HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches()
            && !apiMatcher.matches(request);
      }
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    SecurityProperties.CorsProperties cors = securityProperties.getCors();

    if (Boolean.TRUE.equals(cors.getEnabled())) {
      configuration.setAllowedOriginPatterns(
          Arrays.asList(cors.getAllowed().getOrigins().split(",")));
      configuration.setAllowedMethods(Arrays.asList(cors.getAllowed().getMethods().split(",")));
      configuration.setAllowedHeaders(Arrays.asList(cors.getAllowed().getHeaders().split(",")));
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
        serverProperties.getServlet(), securityProperties.getAppValidatorFilter());
  }
}
