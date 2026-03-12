package dev.barrikeit.service.base;

import java.io.Serializable;
import dev.barrikeit.model.domain.base.BaseEntity;
import dev.barrikeit.service.dto.base.BaseDto;

public interface CrudableService<E extends BaseEntity, I extends Serializable, D extends BaseDto> {
  D save(D dto);

  E save(E entity);

  D update(I id, D dto);

  void delete(I id);
}
