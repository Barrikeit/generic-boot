package org.barrikeit.model.repository;

import java.io.Serializable;
import org.barrikeit.model.domain.GenericEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <b>Generic Repository Interface</b>
 * <p>
 * This interface serves as a base repository for generic entities,
 * extending Spring Data's JpaRepository and JpaSpecificationExecutor.
 * It provides standard data access methods for any entity type
 * that extends {@link GenericEntity}.
 * </p>
 *
 * @param <E> the entity type that extends {@link GenericEntity}.
 * @param <S> the type of the entity's identifier, which must be {@link Serializable}.
 */
@NoRepositoryBean
public interface GenericRepository<E extends GenericEntity, S extends Serializable>
    extends JpaRepository<E, S>, JpaSpecificationExecutor<E> {}
