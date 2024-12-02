package org.barrikeit.service.mapper;

import org.barrikeit.model.domain.User;
import org.barrikeit.service.dto.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends GenericMapper<User, UserDto> {}
