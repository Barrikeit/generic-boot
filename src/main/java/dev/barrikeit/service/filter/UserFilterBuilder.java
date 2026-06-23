package dev.barrikeit.service.filter;

import java.util.List;
import lombok.Getter;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.filter.base.BaseFilterBuilder;
import dev.barrikeit.service.filter.specification.RoleFilterSpecification;
import dev.barrikeit.service.filter.specification.base.SearchCriteria;
import dev.barrikeit.util.constants.EntityConstants;
import dev.barrikeit.util.constants.ExceptionConstants;
import dev.barrikeit.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

@Getter
public class UserFilterBuilder extends BaseFilterBuilder<UserDto, UserFilter> {

  public UserFilterBuilder(Pageable page, String search) {
    super(page, search);
  }

  @Override
  public UserFilter customFilter(List<SearchCriteria> params) {
    UserFilter result = new UserFilter();
    params.forEach(
        param -> {
          switch (param.getKey()) {
            case UserFilter.FILTER_USERNAME -> param.setValue(param.getValue());
            case UserFilter.FILTER_ENABLED -> {
              if (ObjectUtils.nullSafeEquals(param.getValue(), "true")
                  || ObjectUtils.nullSafeEquals(param.getValue(), "false")) {
                param.setValue(Boolean.valueOf((String) param.getValue()));
              } else {
                throw new BadRequestException(
                    ExceptionConstants.ERROR_INVALID_FILTER, param.getKey());
              }
            }
            case UserFilter.FILTER_ROLES ->
                param.setSpecification(
                    RoleFilterSpecification.builder().role((String) param.getValue()).build());
            default -> {}
          }
        });

    return result;
  }

  @Override
  public Sort.Order assignPropertyOrder(String property, Sort.Order order) {
    String propertyOrder = property;
    if (property.equals(EntityConstants.ROLES)) {
      propertyOrder = EntityConstants.ROLES.concat(".").concat(EntityConstants.CODE);
    }

    return new Sort.Order(order.getDirection(), propertyOrder);
  }
}
