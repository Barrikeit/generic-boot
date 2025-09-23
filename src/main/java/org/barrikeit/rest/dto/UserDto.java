package org.barrikeit.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.barrikeit.util.constants.UtilConstants;
import org.barrikeit.util.validation.Alphanumeric;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto extends GenericDto {

  private UUID code;

  @Alphanumeric @NotBlank private String username;

  private String name;

  private String surname1;

  private String surname2;

  @Email @NotBlank private String email;

  // @Password
  @NotBlank private String password;

  private String phone;

  private LocationDto location;

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private LocalDateTime registrationDate;

  private String verificationToken;
  @NotNull @Builder.Default private boolean enabled = false;

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private LocalDateTime loginDate;

  @NotNull @Builder.Default private Integer loginAttempts = 0;

  @NotNull @Builder.Default private boolean banned = false;

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private LocalDateTime banDate;

  private String banReason;

  @Valid private Set<RoleDto> roles;

  @Override
  public String toString() {
    return "UserDto{" + "username='" + username + '\'' + ", email='" + email + '\'' + '}';
  }
}
