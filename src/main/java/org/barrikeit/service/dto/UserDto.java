package org.barrikeit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto extends GenericDto {

  private Long id;
  private String username;
  private String email;

  @Override
  public String toString() {
    return "UserDto{" + "username='" + username + '\'' + ", email='" + email + '\'' + '}';
  }
}
