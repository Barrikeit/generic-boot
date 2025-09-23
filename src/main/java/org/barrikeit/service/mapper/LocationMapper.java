package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.Location;
import org.barrikeit.rest.dto.LocationDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper extends GenericMapper<Location, LocationDto> {

  Location toEntity(LocationDto source);

  LocationDto toDto(Location source);
}
