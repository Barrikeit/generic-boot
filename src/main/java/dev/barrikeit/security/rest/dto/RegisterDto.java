package dev.barrikeit.security.rest.dto;

import dev.barrikeit.util.validation.Alphanumeric;
import dev.barrikeit.util.validation.Password;
import dev.barrikeit.util.validation.Sanitize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class RegisterDto {
  @Alphanumeric @NotBlank @Sanitize private String username;
  @Email @NotBlank private String email;
  @Password @NotBlank private String password;
}
