package org.barrikeit.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.barrikeit.util.validation.Alphanumeric;
import org.barrikeit.util.validation.ValidPassword;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserDto extends GenericDto {

  private Long id;

  @NotEmpty @Alphanumeric private String username;

  @JsonIgnore @ValidPassword private String password;

  @NotEmpty @Email private String email;

  @NotNull private boolean enabled;

  @NotNull private boolean banned;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime banDate;

  @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
  private LocalDateTime loginDate;

  @NotNull private Integer loginAttempts;

  @Valid private HashSet<RoleDto> roles;

  @Override
  public String toString() {
    return "UserDto{" + "username='" + username + '\'' + ", email='" + email + '\'' + '}';
  }
}
