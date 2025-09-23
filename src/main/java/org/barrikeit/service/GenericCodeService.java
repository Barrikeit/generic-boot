package org.barrikeit.service;

import java.io.Serializable;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.GenericCodeEntity;
import org.barrikeit.model.repository.GenericCodeRepository;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.service.mapper.GenericMapper;
import org.barrikeit.util.constants.EntityConstants;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

/**
 * <b>Generic Code Service Class</b>
 *
 * <p>This abstract class provides a generic implementation of service operations for managing
 * entities and their corresponding DTOs. It interacts with a {@link GenericCodeRepository} for data
 * access and uses a {@link GenericMapper} for object mapping between entities and DTOs.
 *
 * @param <E> the entity type that extends {@link GenericCodeEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 * @param <C> the type of the entity's code, which must be {@link Serializable}.
 * @param <D> the DTO type that extends {@link GenericDto}.
 */
@Log4j2
public abstract class GenericCodeService<
        E extends GenericCodeEntity<I, C>,
        I extends Serializable,
        C extends Serializable,
        D extends GenericDto>
    extends GenericService<E, I, D> {

  private final GenericCodeRepository<E, I, C> repository;
  private final GenericMapper<E, D> mapper;

  protected GenericCodeService(
      GenericCodeRepository<E, I, C> repository, GenericMapper<E, D> mapper) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * Retrieves a list of all DTOs sorted by their code.
   *
   * @return a list of DTOs representing all entities.
   */
  @Override
  public List<D> findAll() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, EntityConstants.CODE)).stream()
        .map(this.mapper::toDto)
        .toList();
  }

  /**
   * Retrieves a list of all entities sorted by their code.
   *
   * @return a list of entities.
   */
  @Override
  public List<E> findAllEntity() {
    return repository.findAll(Sort.by(Sort.Direction.ASC, EntityConstants.CODE)).stream().toList();
  }

  /**
   * Retrieves a DTO by its identifier.
   *
   * @param code the code of the entity to retrieve.
   * @return the DTO corresponding to the entity.
   * @throws NotFoundException if the entity is not found.
   */
  public D findByCode(C code) {
    return repository
        .findByCode(code)
        .map(this.mapper::toDto)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.NOT_FOUND, code));
  }

  /**
   * Retrieves an entity by its identifier.
   *
   * @param code the code of the entity to retrieve.
   * @return the entity.
   * @throws NotFoundException if the entity is not found.
   */
  public E findEntityByCode(C code) {
    return repository
        .findByCode(code)
        .orElseThrow(() -> new NotFoundException(ExceptionConstants.NOT_FOUND, code));
  }

  /**
   * Updates an existing entity identified by its code with the provided DTO.
   *
   * @param code the code of the entity to update.
   * @param dto the DTO containing the updated entity information.
   * @return the updated DTO.
   */
  @Transactional
  public D updateByCode(C code, D dto) {
    E entity = findEntityByCode(code);
    mapper.updateEntity(dto, entity);
    return mapper.toDto(repository.save(entity));
  }

  /**
   * Deletes an entity identified by its code.
   *
   * @param code the code of the entity to delete.
   */
  @Transactional
  public void deleteByCode(C code) {
    E entity = findEntityByCode(code);
    repository.delete(entity);
  }
}
