package dev.barrikeit.service.dto;

import dev.barrikeit.service.dto.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DirectionDto extends BaseDto {

  private LocationDto location;

  @NotBlank
  @Size(max = 255)
  private String street;

  @NotBlank
  @Size(max = 20)
  private String number;

  @Size(max = 20)
  private String postalCode;

  @Size(max = 255)
  private String extra;

  @Override
  public String toString() {
    return "DirectionDto{" + "street='" + street + '\'' + ", number='" + number + '\'' + '}';
  }
}
