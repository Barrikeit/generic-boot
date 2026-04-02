package dev.barrikeit.service.mapper.base;

import dev.barrikeit.model.domain.base.BaseEntity;
import dev.barrikeit.service.dto.base.BaseDto;
import org.mapstruct.MappingTarget;

public interface BaseMapper<E extends BaseEntity, D extends BaseDto> {

  D toDto(E source);

  E toEntity(D source);

  void updateEntity(D source, @MappingTarget E target);
}
