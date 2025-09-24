package org.barrikeit.config.security.rest.dto;

import java.util.Date;
import lombok.*;
import org.barrikeit.rest.dto.UserDto;

@Builder
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class JwtDto {
  private String jwt;
  private String refreshToken;
  private Date expireAt;
  private Date expireRefreshAt;
  private UserDto userDto;
}
