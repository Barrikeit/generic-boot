package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.Role;
import org.barrikeit.rest.dto.RoleDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GenericMapper<Role, RoleDto> {

  Role toEntity(RoleDto source);

  RoleDto toDto(Role source);

  @Mapping(target = "id", ignore = true)
  void updateEntity(RoleDto source, @MappingTarget Role target);
}
