package org.barrikeit.rest;

import jakarta.validation.Valid;
import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.rest.dto.Response;
import org.barrikeit.rest.filter.GenericFilter;
import org.barrikeit.service.GenericFilterService;
import org.barrikeit.util.validation.SearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <b>Generic Code Controller Class</b>
 *
 * <p>This abstract class provides common RESTful endpoint implementations for managing entities. It
 * relies on a {@link GenericFilterService} to handle business logic and data access. This controller
 * is designed to work with entities that extend {@link GenericEntity} and their corresponding
 * DTOs that extend {@link GenericDto}.
 *
 * @param <E> the entity type that extends {@link GenericEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 * @param <D> the DTO type that extends {@link GenericDto}.
 */
@Log4j2
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public abstract class GenericFilterController<
        E extends GenericEntity<I>,
        I extends Serializable,
        D extends GenericDto,
        F extends GenericFilter>
    extends GenericController<E, I, D> {

  private final GenericFilterService<E, I, D, F> service;

  protected GenericFilterController(GenericFilterService<E, I, D, F> service) {
    super(service);
    this.service = service;
  }

  /**
   * Método para buscar todas las entidades con un filtro.
   *
   * @param page - Información de paginación para la búsqueda.
   * @param unpaged - Indica si la búsqueda debe ser paginada o no.
   * @param search - Cadena de búsqueda para filtrar las entidades.
   * @return ResponseEntity con la página de resultados de la búsqueda.
   */
  @GetMapping
  public Response<Page<D>> findAll(
      Pageable page,
      @RequestParam(required = false, defaultValue = "false") boolean unpaged,
      @RequestParam(required = false) @Valid @SearchParams String search) {
    return Response.ok(null, service.search(page, unpaged, search));
  }
}
