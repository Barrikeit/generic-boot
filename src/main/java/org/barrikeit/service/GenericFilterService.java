package org.barrikeit.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.model.repository.GenericRepository;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.rest.filter.GenericFilter;
import org.barrikeit.rest.filter.GenericFilterBuilder;
import org.barrikeit.service.mapper.GenericMapper;
import org.barrikeit.util.ReflectionUtil;
import org.barrikeit.util.exceptions.BadRequestException;
import org.barrikeit.util.filter.FilterSpecification;
import org.barrikeit.util.filter.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

/**
 * <b>Generic Code Service Class</b>
 *
 * <p>This abstract class provides a generic implementation of service operations for managing
 * entities and their corresponding DTOs. It interacts with a {@link GenericRepository} for data
 * access and uses a {@link GenericMapper} for object mapping between entities and DTOs.
 *
 * @param <E> the entity type that extends {@link GenericEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 * @param <D> the DTO type that extends {@link GenericDto}.
 * @param <F> the Filter {@link GenericFilter}.
 */
@Log4j2
public abstract class GenericFilterService<
        E extends GenericEntity<I>,
        I extends Serializable,
        D extends GenericDto,
        F extends GenericFilter>
    extends GenericService<E, I, D> {

  private final GenericRepository<E, I> repository;
  private final GenericMapper<E, D> mapper;

  protected GenericFilterService(GenericRepository<E, I> repository, GenericMapper<E, D> mapper) {
    super(repository, mapper);
    this.repository = repository;
    this.mapper = mapper;
  }

  public abstract GenericFilterBuilder<D, F> instanceFilterBuilder(Pageable page, String search);

  /**
   * Realiza una búsqueda paginada o no paginada en base a los criterios de búsqueda proporcionados.
   *
   * @param page El objeto Pageable que contiene la información de paginación.
   * @param unpaged Un booleano que indica si la búsqueda debe ser paginada o no.
   * @param search Una cadena de texto que contiene los criterios de búsqueda.
   * @return Una página de DTOs que cumplen con los criterios de búsqueda.
   */
  public Page<D> search(@NotNull Pageable page, boolean unpaged, String search) {
    Page<E> paged = searchEntity(page, unpaged, search);
    List<D> result = paged.getContent().stream().map(mapper::toDto).toList();
    return new PageImpl<>(result, paged.getPageable(), paged.getTotalElements());
  }

  /**
   * Realiza una búsqueda paginada o no paginada en base a los criterios de búsqueda proporcionados.
   *
   * @param page El objeto Pageable que contiene la información de paginación.
   * @param unpaged Un booleano que indica si la búsqueda debe ser paginada o no.
   * @param search Una cadena de texto que contiene los criterios de búsqueda.
   * @return Una página de Entidades que cumplen con los criterios de búsqueda.
   */
  public Page<E> searchEntity(@NotNull Pageable page, boolean unpaged, String search) {
    GenericFilterBuilder<D, F> filterBuilder = instanceFilterBuilder(page, search);
    return unpaged
        ? searchEntityUnpaged(filterBuilder)
        : repository.findAll(createSpecificationField(filterBuilder), filterBuilder.getPage());
  }

  /**
   * Realiza una búsqueda no paginada y devuelve los resultados.
   *
   * @param filterBuilder El constructor de filtros que contiene los criterios de búsqueda.
   * @return Una página de Entidades que cumplen con los criterios de búsqueda.
   */
  private Page<E> searchEntityUnpaged(GenericFilterBuilder<D, F> filterBuilder) {
    List<E> result =
        repository
            .findAll(createSpecificationField(filterBuilder), filterBuilder.getPage().getSort())
            .stream()
            .toList();
    return new PageImpl<>(result, filterBuilder.getPage(), result.size());
  }

  /**
   * Crea una especificación de búsqueda en base a los filtros proporcionados.
   *
   * @param filterBuilder El constructor de filtros que contiene los criterios de búsqueda.
   * @return Una especificación que puede ser usada para realizar una búsqueda en el repositorio.
   */
  public Specification<E> createSpecificationField(GenericFilterBuilder<D, F> filterBuilder) {
    List<Specification<E>> conditions = new ArrayList<>();

    if (ObjectUtils.isEmpty(filterBuilder.getFilters())) {
      return null;
    }

    // Se recorren los filtros y se añaden las especificaciones correspondientes
    for (SearchCriteria param : filterBuilder.getFilters()) {
      if (ReflectionUtil.getFields(ReflectionUtil.getParameterizedTypeClass(this.getClass(), 0))
          .stream()
          .noneMatch(f -> param.getKey().startsWith(f.getName()))) {
        throw new BadRequestException(
            "{0} no es un filtro válido para la búsqueda.", param.getKey());
      }

      if (!ObjectUtils.isEmpty(param.getValue())) {
        // Si la clave contiene un punto y la especificación es nula, se crea una especificación
        if (param.getKey().contains(".") && param.getSpecification() == null) {
          Specification<E> specification = getSpecification(param);
          conditions.add(specification);
        }
        // Si la clave no contiene un punto y la especificación no es nula, se añade la
        // especificación
        else if (param.getSpecification() != null) {
          @SuppressWarnings("unchecked")
          Specification<E> specification = (Specification<E>) param.getSpecification();
          conditions.add(specification);
        }
        // Si la clave no contiene un punto y la especificación es nula, se añade un filtro
        else {
          conditions.add(new FilterSpecification<>(param));
        }
      }
    }

    return conditions.stream().reduce(Specification::and).orElse(null);
  }

  private static <E extends GenericEntity> Specification<E> getSpecification(SearchCriteria param) {
    return new FilterSpecification<>(param) {
      @Override
      public Predicate toPredicate(
          Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        if (null == getSearchCriteria()) {
          return null;
        }
        String[] parts = getSearchCriteria().getKey().split("\\.");
        Join<E, Object> join = root.join(parts[0], JoinType.INNER);

        String attributeValue = getSearchCriteria().getValue().toString();

        return criteriaBuilder.like(
            criteriaBuilder.lower(join.get(parts[1]).as(String.class)),
            "%" + attributeValue.toLowerCase() + "%");
      }
    };
  }
}
