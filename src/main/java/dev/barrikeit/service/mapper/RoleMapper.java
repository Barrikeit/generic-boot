package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.Role;
import dev.barrikeit.service.dto.RoleDto;
import dev.barrikeit.data.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends BaseMapper<Role, RoleDto> {

  Role toEntity(RoleDto source);

  RoleDto toDto(Role source);

  @Mapping(target = "id", ignore = true)
  void updateEntity(RoleDto source, @MappingTarget Role target);
}
