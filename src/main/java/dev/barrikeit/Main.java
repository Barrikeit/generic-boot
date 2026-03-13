package dev.barrikeit;

import dev.barrikeit.config.ApplicationProperties;
import dev.barrikeit.security.config.SecurityProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Log4j2
@SpringBootApplication
@EnableConfigurationProperties({
  ApplicationProperties.GenericProperties.class,
  ApplicationProperties.ServerProperties.class,
  ApplicationProperties.MailProperties.class,
  SecurityProperties.class,
})
public class Main extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }
}
