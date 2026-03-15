package dev.barrikeit.security.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security", ignoreUnknownFields = false)
public class SecurityProperties {

  private Boolean enabled;
  private Integer maxConcurrentSessions;
  private CorsProperties cors;
  private JwtProperties jwt;
  private AppValidatorFilterProperties appValidatorFilter;

  @Getter
  @Setter
  public static class CorsProperties {
    private Allowed allowed;
    private Boolean enabled;
    private Path path;

    @Getter
    @Setter
    public static class Allowed {
      private String origins;
      private String methods;
      private String headers;
    }

    @Getter
    @Setter
    public static class Path {
      private String pattern;
    }
  }

  @Getter
  @Setter
  public static class JwtProperties {
    @NotBlank String issuer;
    @NotBlank String secret;

    @Min(0)
    long expiration;

    @Min(0)
    long expirationRefresh;
  }

  @Getter
  @Setter
  public static class AppValidatorFilterProperties {
    private Boolean appHeaderNameValidationFilter;
    private String appHeaderName;
    private String appSelfName;
    private String appSecurityName;
  }
}
