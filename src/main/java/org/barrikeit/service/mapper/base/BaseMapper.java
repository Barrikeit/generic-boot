package org.barrikeit.service.mapper.base;

import org.barrikeit.model.domain.base.BaseEntity;
import org.barrikeit.service.dto.base.BaseDto;
import org.mapstruct.MappingTarget;

/**
 * <b>Generic Mapper Interface</b>
 *
 * <p>This interface serves as a base for all mapper classes that convert between generic entities
 * and DTOs (Data Transfer Objects).
 *
 * <p>Standard mappers that extend this interface should be annotated as follows:
 *
 * <ul>
 *   <li><code>@Mapper(componentModel = "spring",
 *     injectionStrategy = InjectionStrategy.CONSTRUCTOR,
 *     unmappedTargetPolicy = ReportingPolicy.IGNORE)</code>
 * </ul>
 *
 * <p>For mappers that utilize other mappers, the annotation should include the <code>builder</code>
 * attribute with the builder disabled, as shown below:
 *
 * <ul>
 *   <li><code>@Mapper(builder = @Builder(disableBuilder = true),
 *     componentModel = "spring",
 *     unmappedTargetPolicy = ReportingPolicy.IGNORE,
 *     uses = {OtroMapper.class})</code>
 * </ul>
 *
 * @param <E> the entity class that extends from the {@link BaseEntity}.
 * @param <D> the DTO class that extends from the {@link BaseDto}.
 */
public interface BaseMapper<E extends BaseEntity, D extends BaseDto> {

  D toDto(E source);

  E toEntity(D source);

  void updateEntity(D source, @MappingTarget E target);
}
