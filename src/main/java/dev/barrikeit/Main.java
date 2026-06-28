package dev.barrikeit;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.logger.LoggerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({
  ApplicationProperties.GenericProperties.class,
  ApplicationProperties.ServerProperties.class,
  ApplicationProperties.MailProperties.class,
})
public class Main extends SpringBootServletInitializer {
  public static void main(String[] args) {
    LoggerConfig.configure();
    SpringApplication.run(Main.class, args);
  }
}
