package org.barrikeit.config;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
  @Component
  @ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
  public static class GenericProperties {
    private String name;
    private String version;
    private String module;
  }

  @Getter
  @Setter
  @Component
  @ConfigurationProperties(prefix = "server", ignoreUnknownFields = false)
  public static class ServerProperties {
    private int port;
    private String contextPath;
    private String apiPath;
    private String activeProfile;
    private boolean forceResponse;
  }

  @Getter
  @Setter
  @Component
  @ConfigurationProperties(prefix = "spring", ignoreUnknownFields = false)
  public static class DatabaseProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private String database;
    private String dialect;
    private String generateDdl;
    private String openInView;
    private String synonyms;
    private String formatSql;
    private String showSql;
    private String defaultSchema;
    private String hbm2ddlAuto;
    private String importFiles;
    private String generateStatistics;
    private String enableLazyLoadNoTrans;

    public Properties properties() {
      Properties properties = new Properties();
      properties.put("hibernate.dialect", getDialect());
      properties.put("hibernate.show_sql", getShowSql());
      properties.put("hibernate.format_sql", getFormatSql());
      properties.put("hibernate.hbm2ddl.auto", getHbm2ddlAuto());
      properties.put("hibernate.hbm2ddl.import_files", getImportFiles());
      properties.put("hibernate.generate_statistics", getGenerateStatistics());
      properties.put("hibernate.jdbc.batch_size", "5");
      properties.put("hibernate.default_batch_fetch_size", "10");
      return properties;
    }
  }
}
