package org.barrikeit.config.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.barrikeit.config.security.config.SecurityProperties;
import org.barrikeit.util.constants.JwtConstants;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtDecoder {
  private final Algorithm hmac256;
  private final String issuer;

  public JwtDecoder(SecurityProperties securityProperties) {
    SecurityProperties.JwtProperties jwtProperties = securityProperties.getJwt();
    this.hmac256 = Algorithm.HMAC256(jwtProperties.getSecret());
    this.issuer = jwtProperties.getIssuer();
  }

  public DecodedJWT validateAndRetrieveDecodedToken(String token) {
    return JWT.require(this.hmac256).withIssuer(this.issuer).build().verify(token);
  }

  /**
   * Cuando el token ha expirado se necesita un metodo alternativo a validateAndRetrieveDecodedToken
   * Util para hacer logouts
   *
   * @param token String con la cadena token codificada
   * @param claim Nombre del claim que queremos obtener del payload
   * @return String con el valor del claim que estamos buscando dentro del payload
   * @throws JsonProcessingException, al crear o procesar un JsonNode creado a partir del payload
   */
  public String getClaimFromExpiredToken(String token, String claim)
      throws JsonProcessingException {
    String resultClaim = "";
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String[] parts = token.split("\\."); // Splitting header, payload and signature
    String payload = new String(decoder.decode(parts[1]));
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(payload);
    resultClaim = node.get(claim).asText();
    return resultClaim;
  }

  public boolean isRefresh(DecodedJWT decodedJWT) {
    if (!decodedJWT.getClaim(JwtConstants.REFRESH).isNull()) {
      return decodedJWT.getClaim(JwtConstants.REFRESH).asBoolean();
    }
    return false;
  }

  public String getSessionIdClaim(DecodedJWT decodedJWT) {
    if (!decodedJWT.getClaim(JwtConstants.SESSION_ID).isNull()) {
      return decodedJWT.getClaim(JwtConstants.SESSION_ID).asString();
    }
    return null;
  }

  public List<String> getRolesClaim(DecodedJWT decodedJWT) {
    if (!decodedJWT.getClaim(JwtConstants.ROLES).isNull()) {
      return decodedJWT.getClaim(JwtConstants.ROLES).asList(String.class);
    }
    return Collections.emptyList();
  }

  public List<SimpleGrantedAuthority> getAuthorities(String token) {
    ArrayList<SimpleGrantedAuthority> result = new ArrayList<>();
    DecodedJWT decodedJWT = validateAndRetrieveDecodedToken(token);
    List<String> rolesClaim = getRolesClaim(decodedJWT);
    for (String role : rolesClaim) {
      result.add(new SimpleGrantedAuthority(role));
    }
    return result;
  }

  public String getSessionIdFromToken(String jwt) {
    String sessionId = "";
    try {
      DecodedJWT decodedJWT = this.validateAndRetrieveDecodedToken(jwt);
      sessionId = this.getSessionIdClaim(decodedJWT);
    } catch (TokenExpiredException e) {
      // Si el token ha expirado se requiere otro metodo para decodificar
      try {
        sessionId = this.getClaimFromExpiredToken(jwt, JwtConstants.SESSION_ID);
      } catch (JsonProcessingException jsonProcessingException) {
        throw new JWTDecodeException(
            "Se ha producido un error al decodificar la peticion de la session de un token expirado: "
                + jwt);
      }
    }

    return sessionId;
  }
}
