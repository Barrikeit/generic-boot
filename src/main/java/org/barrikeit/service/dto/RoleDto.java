package org.barrikeit.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.barrikeit.util.validation.Alphanumeric;
import org.barrikeit.util.validation.Sanitize;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class RoleDto extends GenericDto {

  @NotNull
  @Size(max = 2)
  @Sanitize
  @Alphanumeric
  String code;

  @NotNull
  @Size(max = 50)
  @Sanitize
  @Alphanumeric
  String role;
}
