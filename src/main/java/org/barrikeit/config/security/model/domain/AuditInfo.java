package org.barrikeit.config.security.model.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class AuditInfo {
  private String application;
  private String hostUser;
  private String host;

  private String token;

  public AuditInfo(String application, String hostUser, String host) {
    this.application = application;
    this.hostUser = hostUser;
    this.host = host;
  }

  public String toHeaderString() {
    return application + "#" + hostUser + "#" + host;
  }
}
