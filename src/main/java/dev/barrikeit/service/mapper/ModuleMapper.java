package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.Module;
import dev.barrikeit.service.dto.ModuleDto;
import dev.barrikeit.data.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModuleMapper extends BaseMapper<Module, ModuleDto> {

  Module toEntity(ModuleDto source);

  ModuleDto toDto(Module source);

  @Mapping(target = "id", ignore = true)
  void updateEntity(ModuleDto source, @MappingTarget Module target);
}
