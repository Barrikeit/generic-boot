package dev.barrikeit.model.repository.base;

import java.io.Serializable;
import dev.barrikeit.model.domain.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <b>Base CRUD Repository</b>
 *
 * <p>Generic repository that provides CRUD operations for any entity extending {@link BaseEntity}.
 *
 * @param <E> the entity type extending {@link BaseEntity}
 * @param <I> the type of the entity's identifier (must be {@link Serializable})
 */
@NoRepositoryBean
public interface CrudBaseRepository<E extends BaseEntity, I extends Serializable>
    extends JpaRepository<E, I> {}
