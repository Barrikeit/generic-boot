package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.Location;
import org.barrikeit.service.dto.LocationDto;
import org.barrikeit.service.mapper.base.BaseMapper;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper extends BaseMapper<Location, LocationDto> {

  Location toEntity(LocationDto source);

  LocationDto toDto(Location source);
}
