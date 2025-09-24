package org.barrikeit.rest;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.GenericCodeEntity;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.rest.dto.Response;
import org.barrikeit.service.GenericCodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <b>Generic Code Controller Class</b>
 *
 * <p>This abstract class provides common RESTful endpoint implementations for managing entities. It
 * relies on a {@link GenericCodeService} to handle business logic and data access. This controller
 * is designed to work with entities that extend {@link GenericCodeEntity} and their corresponding
 * DTOs that extend {@link GenericDto}.
 *
 * @param <E> the entity type that extends {@link GenericCodeEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 * @param <C> the type of the entity's code, which must be {@link Serializable}.
 * @param <D> the DTO type that extends {@link GenericDto}.
 */
@Log4j2
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public abstract class GenericCodeController<
        E extends GenericCodeEntity<I, C>,
        I extends Serializable,
        C extends Serializable,
        D extends GenericDto>
    extends GenericController<E, I, D> {

  private final GenericCodeService<E, I, C, D> service;

  protected GenericCodeController(GenericCodeService<E, I, C, D> service) {
    super(service);
    this.service = service;
  }

  /**
   * Retrieves a specific DTO by its code.
   *
   * @param code the code of the entity to retrieve.
   * @return a response entity containing the requested DTO.
   */
  @GetMapping("/code/{code}")
  public Response<D> findByCode(@PathVariable("code") C code) {
    return Response.ok(service.findByCode(code));
  }

  /**
   * Updates an existing entity identified by its code with the provided DTO.
   *
   * @param code the code of the entity to update.
   * @param dto the DTO containing the updated entity information.
   * @return a response entity containing the updated DTO.
   */
  @PutMapping("/code/{code}/update")
  public Response<D> updateByCode(@PathVariable("code") C code, @RequestBody D dto) {
    return Response.ok(service.updateByCode(code, dto));
  }

  /**
   * Deletes an entity identified by its code.
   *
   * @param code the code of the entity to delete.
   * @return a response entity indicating the operation's result.
   */
  @DeleteMapping("/code/{code}")
  public Response<Void> deleteByCode(@PathVariable("code") C code) {
    service.deleteByCode(code);
    return Response.ok("Eliminado con éxito", null);
  }
}
