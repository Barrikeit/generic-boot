package org.barrikeit.config.security.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.barrikeit.util.validation.Alphanumeric;
import org.barrikeit.util.validation.Sanitize;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginDto {
  @Alphanumeric @NotBlank @Sanitize private String username;
  // @Password
  @NotBlank @Sanitize private String password;
}
