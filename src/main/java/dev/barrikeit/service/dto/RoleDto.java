package dev.barrikeit.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import dev.barrikeit.service.dto.base.BaseDto;
import dev.barrikeit.util.validation.Alphanumeric;
import dev.barrikeit.util.validation.Sanitize;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleDto extends BaseDto {

  @NotNull
  @Size(max = 2)
  @Sanitize
  @Alphanumeric
  String code;

  @NotNull
  @Size(max = 50)
  @Sanitize
  @Alphanumeric
  String name;
}
