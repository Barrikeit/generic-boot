package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.User;
import org.barrikeit.service.dto.UserDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends GenericMapper<User, UserDto> {

  User toEntity(UserDto source);

  @Mapping(target = "password", ignore = true)
  UserDto toDto(User source);

  void updateEntity(UserDto source, @MappingTarget User target);

  @Mapping(target = "password", ignore = true)
  void updateDto(UserDto source, @MappingTarget User target);

  @Mapping(target = "password", ignore = true)
  void updateDto(UserDto source, @MappingTarget UserDto target);
}
