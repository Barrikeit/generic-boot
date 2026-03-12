package dev.barrikeit.service.filter;

import jakarta.persistence.OrderColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import dev.barrikeit.service.filter.base.BaseFilter;

@Getter
@Setter
@NoArgsConstructor
public class UserFilter extends BaseFilter {

  public static final String FILTER_USERNAME = "username";
  public static final String FILTER_ENABLED = "enabled";
  public static final String FILTER_ROLES = "roles";

  @OrderColumn private String username;
  @OrderColumn private String name;
  @OrderColumn private String email;
  @OrderColumn private Boolean enabled;
  @OrderColumn private Boolean banned;
  @OrderColumn private String roles;
}
