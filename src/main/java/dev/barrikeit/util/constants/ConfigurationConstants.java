package dev.barrikeit.util.constants;

public class ConfigurationConstants {
  private ConfigurationConstants() {
    throw new IllegalStateException("Constants class");
  }

  public static final String DEFAULT_SERVLET_NAME = "/generic";

  public static final String COMPONENT_PACKAGE_TO_SCAN = "dev.barrikeit";
  public static final String APPLICATION_PACKAGE = "dev.barrikeit.application";

  public static final String CONFIG_PACKAGE = "dev.barrikeit.config";
  public static final String REST_PACKAGE = "dev.barrikeit.rest";
  public static final String SERVICES_PACKAGE = "dev.barrikeit.service";
  public static final String REPOSITORIES_PACKAGE = "dev.barrikeit.model.repository";
  public static final String ENTITIES_PACKAGE = "dev.barrikeit.model.domain";

  public static final String[] CONFIG_LOCATIONS = {"/", "/config/", "/configuration/"};
  public static final String[] CONFIG_EXTENSIONS = {"properties", "yml", "yaml"};

  public static final String SPRING_PROFILE_TEST = "test";
  public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
  public static final String SPRING_PROFILE_PRODUCTION = "prod";
}
