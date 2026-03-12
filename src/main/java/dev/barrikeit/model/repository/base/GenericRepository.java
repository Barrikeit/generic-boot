package dev.barrikeit.model.repository.base;

import java.io.Serializable;
import dev.barrikeit.model.domain.base.GenericEntity;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <b>Generic Repository Interface</b>
 *
 * <p>This interface serves as a base repository for generic entities, extending Spring Data's
 * JpaRepository and JpaSpecificationExecutor. It provides standard data access methods for any
 * entity type that extends {@link GenericEntity}.
 *
 * @param <E> the entity type that extends {@link GenericEntity}.
 * @param <I> the type of the entity's identifier, which must be {@link Serializable}.
 */
@NoRepositoryBean
public interface GenericRepository<E extends GenericEntity<I>, I extends Serializable>
    extends BaseRepository<E, I> {}
