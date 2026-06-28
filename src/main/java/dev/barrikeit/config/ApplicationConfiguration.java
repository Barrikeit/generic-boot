package dev.barrikeit.config;

import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.ServletContext;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration implements ServletContextInitializer {

  private final Environment env;

  @Override
  public void onStartup(ServletContext servletContext) {
    if (env.getActiveProfiles().length != 0) {
      log.info(
          "Application configuration, using profiles: {}",
          Arrays.toString(env.getActiveProfiles()));
    }
    log.info("Application fully configured");
  }

  @Bean
  ObservationRegistryCustomizer<ObservationRegistry> skipWatchdogObservations() {
    PathMatcher pathMatcher = new AntPathMatcher("/");
    return registry ->
        registry
            .observationConfig()
            .observationPredicate(
                (name, context) -> {
                  if (context instanceof ServerRequestObservationContext ctx) {
                    return !pathMatcher.match("/**/watchdog/**", ctx.getCarrier().getRequestURI());
                  }
                  return true;
                });
  }

  @Bean
  ObservationRegistryCustomizer<ObservationRegistry> skipSecuritySpansFromObservation() {
    return registry ->
        registry
            .observationConfig()
            .observationPredicate((name, context) -> !name.startsWith("spring.security"));
  }
}
