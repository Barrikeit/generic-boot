package dev.barrikeit.service.dto;

import dev.barrikeit.service.dto.base.BaseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class VersionDto extends BaseDto {
  private String name;
  private String version;
  private String build;
  private String environment;
}
