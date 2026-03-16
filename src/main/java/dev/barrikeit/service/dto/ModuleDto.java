package dev.barrikeit.service.dto;

import dev.barrikeit.service.dto.base.BaseDto;
import dev.barrikeit.util.validation.Alphanumeric;
import dev.barrikeit.util.validation.Sanitize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ModuleDto extends BaseDto {

  @NotNull
  @Size(max = 3)
  @Sanitize
  @Alphanumeric
  String code;

  @NotNull
  @Size(max = 200)
  @Sanitize
  String name;
}
