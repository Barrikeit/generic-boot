package org.barrikeit.service;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.model.repository.GenericRepository;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.service.mapper.GenericMapper;
import org.barrikeit.util.constants.EntityConstants;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Generic Service Class</b>
 *
 * <p>This abstract class provides a generic implementation of service operations for managing
 * entities and their corresponding DTOs. It interacts with a {@link GenericRepository} for data
 * access and uses a {@link GenericMapper} for object mapping between entities and DTOs.
 *
 * @param <E> the entity type that extends {@link GenericEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 * @param <D> the DTO type that extends {@link GenericDto}.
 */
@Log4j2
@AllArgsConstructor
public abstract class GenericService<
    E extends GenericEntity<I>, I extends Serializable, D extends GenericDto> {
  private final GenericRepository<E, I> repository;
  private final GenericMapper<E, D> mapper;

  /**
   * Retrieves a list of all DTOs sorted by their identifier.
   *
   * @return a list of DTOs representing all entities.
   */
  public List<D> findAll() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, EntityConstants.ID)).stream()
        .map(mapper::toDto)
        .toList();
  }

  /**
   * Retrieves a list of all DTOs sorted by the specified sort criteria.
   *
   * @param sort the sorting criteria.
   * @return a list of DTOs representing all entities.
   */
  public List<D> findAll(Sort sort) {
    return repository.findAll(sort).stream().map(mapper::toDto).toList();
  }

  /**
   * Retrieves a list of all entities sorted by their identifier.
   *
   * @return a list of entities.
   */
  public List<E> findAllEntity() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, EntityConstants.ID)).stream().toList();
  }

  /**
   * Retrieves a list of all entities sorted by the specified sort criteria.
   *
   * @param sort the sorting criteria.
   * @return a list of entities.
   */
  public List<E> findAllEntity(Sort sort) {
    return repository.findAll(sort).stream().toList();
  }

  /**
   * Retrieves a DTO by its identifier.
   *
   * @param id the identifier of the entity to retrieve.
   * @return the DTO corresponding to the entity.
   * @throws NotFoundException if the entity is not found.
   */
  public D find(I id) {
    return repository
        .findById(id)
        .map(mapper::toDto)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.NOT_FOUND, id));
  }

  /**
   * Retrieves an entity by its identifier.
   *
   * @param id the identifier of the entity to retrieve.
   * @return the entity.
   * @throws NotFoundException if the entity is not found.
   */
  public E findEntity(I id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.NOT_FOUND, id));
  }

  /**
   * Saves a new entity represented by the provided DTO.
   *
   * @param dto the DTO representing the entity to save.
   * @return the saved DTO.
   */
  @Transactional
  public D save(D dto) {
    E entity = mapper.toEntity(dto);
    entity = repository.save(entity);
    return mapper.toDto(entity);
  }

  /**
   * Saves a new entity directly.
   *
   * @param entity the entity to save.
   * @return the saved entity.
   */
  @Transactional
  public E save(E entity) {
    return repository.save(entity);
  }

  /**
   * Updates an existing entity identified by its identifier with the provided DTO.
   *
   * @param id the identifier of the entity to update.
   * @param dto the DTO containing the updated entity information.
   * @return the updated DTO.
   */
  @Transactional
  public D update(I id, D dto) {
    E entity = findEntity(id);
    mapper.updateEntity(dto, entity);
    return mapper.toDto(repository.save(entity));
  }

  /**
   * Deletes an entity identified by its identifier.
   *
   * @param id the identifier of the entity to delete.
   */
  @Transactional
  public void delete(I id) {
    repository.deleteById(id);
  }
}
