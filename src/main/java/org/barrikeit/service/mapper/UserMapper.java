package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.User;
import org.barrikeit.rest.dto.UserDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {RoleMapper.class, LocationMapper.class})
public interface UserMapper extends GenericMapper<User, UserDto> {

  User toEntity(UserDto source);

  @Mapping(target = "password", ignore = true)
  UserDto toDto(User source);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "code", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "roles", ignore = true)
  void updateEntity(UserDto source, @MappingTarget User target);

  @Mapping(target = "password", ignore = true)
  void updateDto(UserDto source, @MappingTarget User target);

  @Mapping(target = "password", ignore = true)
  void updateDto(UserDto source, @MappingTarget UserDto target);
}
