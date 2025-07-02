package org.barrikeit.config.security.service.dto;

import lombok.*;
import org.barrikeit.service.dto.UserDto;

import java.util.Date;

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
