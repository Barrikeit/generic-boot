package org.barrikeit.config;

import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements ServletContextInitializer {

  private final Environment env;

  @Override
  public void onStartup(ServletContext servletContext) {
    if (env.getActiveProfiles().length != 0) {
      log.info(
          "Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
    }
    log.info("Web application fully configured");
  }

  @Bean
  ObservationRegistry skipSpringActuatorObservations() {
    PathMatcher pathMatcher = new AntPathMatcher("/");
    ObservationRegistry observationRegistry = ObservationRegistry.create();
    observationRegistry
        .observationConfig()
        .observationPredicate(
            (name, context) -> {
              if (context
                  instanceof ServerRequestObservationContext serverrequestobservationcontext) {
                return !(pathMatcher.match(
                    "/**/management/**",
                    serverrequestobservationcontext.getCarrier().getRequestURI()));
              } else {
                return true;
              }
            });
    return observationRegistry;
  }

  @Bean
  ObservationRegistryCustomizer<ObservationRegistry> skipSecuritySpansFromObservation() {
    return registry ->
        registry
            .observationConfig()
            .observationPredicate((name, context) -> !name.startsWith("spring.security"));
  }
}
