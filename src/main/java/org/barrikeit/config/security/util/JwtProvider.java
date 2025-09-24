package org.barrikeit.config.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.barrikeit.config.security.config.SecurityProperties;
import org.barrikeit.config.security.model.domain.BasicUserDetails;
import org.barrikeit.config.security.model.domain.Jwt;
import org.barrikeit.util.TimeUtil;
import org.barrikeit.util.constants.JwtConstants;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  private final Algorithm hmac256;
  private final String issuer;
  private final long jwtExpirationInSec;
  private final long jwtExpirationRefreshInSec;

  public JwtProvider(SecurityProperties securityProperties) {
    SecurityProperties.JwtProperties jwtProperties = securityProperties.getJwt();
    this.hmac256 = Algorithm.HMAC256(jwtProperties.getSecret());
    this.issuer = jwtProperties.getIssuer();
    this.jwtExpirationInSec = jwtProperties.getExpiration();
    this.jwtExpirationRefreshInSec = jwtProperties.getExpirationRefresh();
  }

  public Jwt generateToken(BasicUserDetails userDetails, String sessionId) {
    return createToken(userDetails, jwtExpirationInSec, false, sessionId);
  }

  public Jwt generateRefreshToken(BasicUserDetails userDetails, String sessionId) {
    return createToken(userDetails, jwtExpirationRefreshInSec, true, sessionId);
  }

  public Jwt decode(String token) {
    DecodedJWT decoded = JWT.require(hmac256).withIssuer(issuer).build().verify(token);
    return Jwt.builder()
        .expiresAt(decoded.getExpiresAt())
        .issuedAt(decoded.getIssuedAt())
        .issuer(decoded.getIssuer())
        .scopes(decoded.getClaim(JwtConstants.SCOPES).asList(String.class))
        .refresh(decoded.getClaim(JwtConstants.REFRESH).asBoolean())
        .subject(decoded.getSubject())
        .build();
  }

  public String validateTokenAndRetrieveSubject(String token) {
    return JWT.require(hmac256).withIssuer(issuer).build().verify(token).getSubject();
  }

  private Jwt createToken(
      BasicUserDetails userDetails, long expiresAt, boolean isRefresh, String sessionId) {
    List<String> roles = userDetails.getRolesNames();
    List<String> authorities = userDetails.getAuthorityNames();
    Instant now = TimeUtil.instantNow();

    return Jwt.builder()
        .sessionId(sessionId)
        .subject(userDetails.getUsername())
        .roles(roles)
        .modules(authorities)
        .issuer(issuer)
        .issuedAt(Date.from(now))
        .expiresAt(Date.from(now.plusSeconds(expiresAt)))
        .refresh(isRefresh)
        .hmac256(hmac256)
        .build();
  }
}
