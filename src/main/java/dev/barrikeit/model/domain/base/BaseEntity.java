package dev.barrikeit.model.domain.base;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {}
