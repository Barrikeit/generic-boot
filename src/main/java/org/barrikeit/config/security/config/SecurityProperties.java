package org.barrikeit.config.security.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security", ignoreUnknownFields = false)
public class SecurityProperties {

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
      private String methods;
      private String headers;
      private String origins;
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
    private String appSelfName;
    private Boolean appHeaderNameValidationFilter;
    private String appHeaderName;
    private String appSecurityName;
  }
}
