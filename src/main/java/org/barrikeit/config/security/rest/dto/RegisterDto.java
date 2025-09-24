package org.barrikeit.config.security.rest.dto;

import jakarta.validation.constraints.Email;
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
public class RegisterDto {
  @Alphanumeric @NotBlank @Sanitize private String username;
  @Email @NotBlank private String email;
  // @Password
  @NotBlank private String password;
}
