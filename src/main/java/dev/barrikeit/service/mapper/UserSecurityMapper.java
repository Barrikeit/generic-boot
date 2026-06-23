package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.UserSecurity;
import dev.barrikeit.service.dto.UserSecurityDto;
import dev.barrikeit.data.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSecurityMapper extends BaseMapper<UserSecurity, UserSecurityDto> {

  @Mapping(target = "owner", ignore = true)
  UserSecurity toEntity(UserSecurityDto source);

  UserSecurityDto toDto(UserSecurity source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "owner", ignore = true)
  void updateEntity(UserSecurityDto source, @MappingTarget UserSecurity target);
}
