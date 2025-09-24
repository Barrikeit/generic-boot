package org.barrikeit.config.security.model.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.List;
import lombok.*;
import org.barrikeit.util.constants.JwtConstants;

@Builder
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Jwt {
  private final String sessionId;
  private final String subject;
  private final List<String> roles;
  private final List<String> scopes;
  private final List<String> modules;
  private final Date issuedAt;
  private final Date expiresAt;
  private final String issuer;
  private final boolean refresh;
  private final Algorithm hmac256;
  private String jwtCache;

  public String getJwtCache(boolean refresh) {
    if (jwtCache == null) {
      jwtCache =
              JWT.create()
                      .withSubject(subject)
                      .withIssuedAt(issuedAt)
                      .withExpiresAt(expiresAt)
                      .withIssuer(issuer)
                      .withClaim(JwtConstants.SESSION_ID, sessionId)
                      .withClaim(JwtConstants.ROLES, roles)
                      .withClaim(JwtConstants.SCOPES, scopes)
                      .withClaim(JwtConstants.MODULES, modules)
                      .withClaim(JwtConstants.REFRESH, refresh)
                      .sign(hmac256);
    }
    return jwtCache;
  }
}
