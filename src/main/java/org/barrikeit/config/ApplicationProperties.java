package org.barrikeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <b>Configuration Properties Class</b>
 *
 * <p>This class is responsible for reading the database configuration values defined in the
 * application's configuration file (like .yml, .yaml, or .properties). It supports two main
 * approaches for obtaining these values:
 *
 * <ul>
 *   <li>Using {@code @ConfigurationProperties(prefix = "<field>")}: This binds all properties under
 *       the specified prefix directly to the fields of the class. You need to enable this in the
 *       main class by adding {@code @EnableConfigurationProperties(Configuration.class)}.
 *   <li>Using {@code @Value("${<field>.<field>}")}: This directly injects individual values from
 *       the configuration file into the respective fields of this class.
 * </ul>
 *
 * <p>This class uses the {@code @Value} annotation approach to inject different type of properties
 * such as ServerProperties or DatabaseProperties.
 */
public class ApplicationProperties {
  public ApplicationProperties() {}

  @Getter
  @Setter
  @ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
  public static class GenericProperties {
    private String name;
    private String version;
    private String build;
    private String buildTime;
  }

  @Getter
  @Setter
  @ConfigurationProperties(prefix = "server", ignoreUnknownFields = false)
  public static class ServerProperties {
    private int port;
    private String timeZone;
    private String activeProfile;
    private ServletProperties servlet;

    @Getter
    @Setter
    public static class ServletProperties {
      private String contextPath;
      private String apiPath;
      private EncodingProperties encoding;

      @Getter
      @Setter
      public static class EncodingProperties {
        private String charset;
        private boolean enabled;
        private boolean force;
        private boolean forceResponse;
      }
    }
  }

  @Getter
  @Setter
  @ConfigurationProperties(prefix = "mail", ignoreUnknownFields = false)
  public static class MailProperties {
    private String host;
    private int port;
    private String user;
    private String pass;
    private String from;
    private String activacionUrl;
    private Properties properties;

    @Getter
    @Setter
    public static class Properties {
      private String protocol;
      private String auth;
      private String starttls;
      private String debug;
    }
  }
}
