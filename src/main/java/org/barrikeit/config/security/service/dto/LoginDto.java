package org.barrikeit.config.security.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.barrikeit.util.validation.Alphanumeric;
import org.barrikeit.util.validation.Sanitize;
import org.barrikeit.util.validation.ValidPassword;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class LoginDto {

  private Long id;

  @NotBlank @Sanitize @Alphanumeric private String username;

  // @ValidPassword
  @NotBlank @Sanitize private String password;
}
