package dev.barrikeit.security.rest.dto;

import dev.barrikeit.service.dto.UserDto;
import java.util.Date;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class JwtDto {
  private String token;
  private String refreshToken;
  private Date expireAt;
  private Date expireRefreshAt;
  private UserDto userDto;
}
