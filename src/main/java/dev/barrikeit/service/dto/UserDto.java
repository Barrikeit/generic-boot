package dev.barrikeit.service.dto;

import dev.barrikeit.service.dto.base.BaseDto;
import dev.barrikeit.util.validation.Alphanumeric;
import dev.barrikeit.util.validation.Password;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto extends BaseDto {

  private UUID id;

  @Alphanumeric @NotBlank private String username;

  private String name;

  private String surname1;

  private String surname2;

  @Email @NotBlank private String email;

  @Password @NotBlank private String password;

  private String phone;

  private DirectionDto direction;

  private UserSecurityDto security;

  @Valid private Set<RoleDto> roles;

  @Override
  public String toString() {
    return "UserDto{" + "username='" + username + '\'' + ", email='" + email + '\'' + '}';
  }
}
