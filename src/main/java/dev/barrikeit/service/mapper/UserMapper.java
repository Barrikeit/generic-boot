package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.User;
import dev.barrikeit.service.dto.UserDto;
import dev.barrikeit.service.mapper.base.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {RoleMapper.class, DirectionMapper.class, UserSecurityMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {

  User toEntity(UserDto source);

  @Mapping(target = "password", ignore = true)
  UserDto toDto(User source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "security", ignore = true)
  void updateEntity(UserDto source, @MappingTarget User target);

  @Mapping(target = "password", ignore = true)
  void updateDto(UserDto source, @MappingTarget UserDto target);
}
