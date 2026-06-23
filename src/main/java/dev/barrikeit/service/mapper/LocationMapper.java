package dev.barrikeit.service.mapper;

import dev.barrikeit.model.domain.Location;
import dev.barrikeit.service.dto.LocationDto;
import dev.barrikeit.data.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper extends BaseMapper<Location, LocationDto> {

  Location toEntity(LocationDto source);

  LocationDto toDto(Location source);

  @Mapping(target = "id", ignore = true)
  void updateEntity(LocationDto source, @MappingTarget Location target);
}
