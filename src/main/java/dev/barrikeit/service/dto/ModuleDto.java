package dev.barrikeit.service.dto;

import dev.barrikeit.data.dto.BaseDto;
import dev.barrikeit.validation.Alphanumeric;
import dev.barrikeit.validation.Sanitize;
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
