package org.barrikeit.service.base;

import org.barrikeit.service.dto.base.BaseDto;
import org.barrikeit.service.filter.base.BaseFilter;
import org.barrikeit.service.filter.base.BaseFilterBuilder;
import org.springframework.data.domain.Pageable;

public interface FilterableService<D extends BaseDto, F extends BaseFilter> {
  BaseFilterBuilder<D, F> instanceFilterBuilder(Pageable page, String search);
}
