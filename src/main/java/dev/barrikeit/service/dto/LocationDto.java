package dev.barrikeit.service.dto;

import dev.barrikeit.data.dto.BaseDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LocationDto extends BaseDto {

  private String code;

  private String country;

  private String city;

  @Override
  public String toString() {
    return "LocationDto{"
        + "code='"
        + code
        + '\''
        + ", country='"
        + country
        + '\''
        + ", city='"
        + city
        + '\''
        + '}';
  }
}
