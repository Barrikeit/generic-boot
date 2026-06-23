package dev.barrikeit.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.barrikeit.data.dto.BaseDto;
import dev.barrikeit.util.constants.UtilConstants;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserSecurityDto extends BaseDto {

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private OffsetDateTime registrationDate;

  private String verificationToken;

  @NotNull @Builder.Default private boolean enabled = false;

  @NotNull @Builder.Default private Integer loginAttempts = 0;

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private OffsetDateTime loginDate;

  @NotNull @Builder.Default private boolean banned = false;

  @JsonFormat(pattern = UtilConstants.PATTERN_DATE_TIME)
  private OffsetDateTime banDate;

  private String banReason;
}
