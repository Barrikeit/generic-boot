package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.service.dto.GenericDto;
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
 * @param <E> the entity class that extends the generic entity.
 * @param <D> the DTO class that extends the generic DTO.
 */
public interface GenericMapper<E extends GenericEntity, D extends GenericDto> {

  D toDto(E source);

  E toEntity(D source);

  void updateEntity(D source, @MappingTarget E target);
}
