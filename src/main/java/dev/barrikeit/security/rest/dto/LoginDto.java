package dev.barrikeit.security.rest.dto;

import dev.barrikeit.util.validation.Alphanumeric;
import dev.barrikeit.util.validation.SafeInput;
import dev.barrikeit.util.validation.Sanitize;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginDto {
  @Alphanumeric @NotBlank @Sanitize private String username;
  @SafeInput @NotBlank @Sanitize private String password;
}
