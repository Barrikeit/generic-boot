package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.Direction;
import dev.barrikeit.service.dto.DirectionDto;
import dev.barrikeit.service.mapper.base.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {LocationMapper.class})
public interface DirectionMapper extends BaseMapper<Direction, DirectionDto> {

  Direction toEntity(DirectionDto source);

  DirectionDto toDto(Direction source);

  @Mapping(target = "id", ignore = true)
  void updateEntity(DirectionDto source, @MappingTarget Direction target);
}
