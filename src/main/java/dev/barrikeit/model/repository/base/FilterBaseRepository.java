package dev.barrikeit.model.repository.base;

import dev.barrikeit.model.domain.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <b>Base Filter Repository</b>
 *
 * <p>Provides filtering and criteria query capabilities for entities extending {@link BaseEntity},
 * using {@link org.springframework.data.jpa.domain.Specification}.
 *
 * @param <E> the entity type extending {@link BaseEntity}
 */
@NoRepositoryBean
public interface FilterBaseRepository<E extends BaseEntity> extends JpaSpecificationExecutor<E> {}
