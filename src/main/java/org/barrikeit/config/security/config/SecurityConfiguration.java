package org.barrikeit.config.security.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.config.security.config.filter.AppHeaderValidatorFilter;
import org.barrikeit.config.security.config.filter.JwtFilter;
import org.barrikeit.config.security.service.SessionService;
import org.barrikeit.config.security.util.JwtDecoder;
import org.barrikeit.config.security.util.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Import(SecurityExceptionHandler.class)
public class SecurityConfiguration<S extends Session> {
  private final SecurityProperties securityProperties;
  private final SecurityExceptionHandler exceptionHandler;
  private final JwtProvider jwtProvider;
  private final JwtDecoder jwtDecoder;

  private final SessionService<S> sessionService;
  private final FindByIndexNameSessionRepository<S> sessionRepository;

  @Value("${spring.security.http.session-management.concurrency-control.max-sessions}")
  private Integer maxNumberConcurrentSessionsUser;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(maxNumberConcurrentSessionsUser)
                    .sessionRegistry(sessionRegistry()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        antMatcher("/images/**"),
                        antMatcher("/api/v1/error"),
                        antMatcher("/api/v1/openapi/**"),
                        antMatcher("/api/v1/version/**"),
                        antMatcher("/api/v1/auth/**"))
                    .permitAll()
                    .requestMatchers(antMatcher("/**/management/**"))
                    .hasRole("MANAGEMENT")
                    .requestMatchers(antMatcher("/api/v1/users/**"), antMatcher("/api/v1/roles/**"))
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults());

    http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(appHeaderValidatorFilter(), JwtFilter.class);

    return http.build();
  }

  @Bean
  public RequestMatcher csrfRequestMatcher() {
    return new RequestMatcher() {
      private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|POST|PUT|DELETE)$");
      private final RegexRequestMatcher apiMatcher = new RegexRequestMatcher("^/api/.*", null);

      @Override
      public boolean matches(final HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches()
            && !apiMatcher.matches(request);
      }
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    SecurityProperties.CorsProperties cors = securityProperties.getCors();

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    if (Boolean.TRUE.equals(cors.getEnabled())) {
      configuration.setAllowedOrigins(Arrays.asList(cors.getAllowed().getOrigins().split(",")));
      configuration.setAllowedMethods(Arrays.asList(cors.getAllowed().getMethods().split(",")));
      configuration.setAllowedHeaders(Arrays.asList(cors.getAllowed().getHeaders().split(",")));
      configuration.setAllowCredentials(true);
      source.registerCorsConfiguration(cors.getPath().getPattern(), configuration);
    }
    return source;
  }

  @Bean
  public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
    return new SpringSessionBackedSessionRegistry<>(sessionRepository);
  }

  @Bean
  JwtFilter jwtFilter() {
    return new JwtFilter(jwtProvider, jwtDecoder, sessionService);
  }

  @Bean
  AppHeaderValidatorFilter appHeaderValidatorFilter() {
    return new AppHeaderValidatorFilter(securityProperties.getAppValidatorFilter());
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
