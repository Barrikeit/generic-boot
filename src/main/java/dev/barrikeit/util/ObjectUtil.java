package dev.barrikeit.util;

import dev.barrikeit.model.domain.base.GenericEntity;
import dev.barrikeit.service.dto.base.BaseDto;
import dev.barrikeit.util.exceptions.UnexpectedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

@Log4j2
public class ObjectUtil {
  private ObjectUtil() {
    throw new IllegalStateException("ObjectUtil class");
  }

  public static boolean isEntityOrDto(Class<?> clazz) {
    return GenericEntity.class.isAssignableFrom(clazz) || BaseDto.class.isAssignableFrom(clazz);
  }

  public static boolean isSimpleType(Class<?> type) {
    return type.isPrimitive()
        || type.isEnum()
        || type.equals(String.class)
        || Number.class.isAssignableFrom(type)
        || Boolean.class.isAssignableFrom(type)
        || Date.class.isAssignableFrom(type)
        || type.equals(LocalDate.class)
        || type.equals(LocalDateTime.class)
        || type.equals(OffsetDateTime.class);
  }

  /** Convierte el valor de texto en booleano, lanzando excepción si el formato es inválido. */
  public static boolean parseBooleanValue(String value) {
    if (ObjectUtils.isEmpty(value)) {
      return false;
    }
    String normalized = value.trim().toLowerCase();
    if ("true".equals(normalized) || "1".equals(normalized)) {
      return true;
    } else if ("false".equals(normalized) || "0".equals(normalized)) {
      return false;
    }
    throw new UnexpectedException("Unsupported cast type: {}", value);
  }

  /**
   * Convierte un valor al tipo especificado, realizando las transformaciones necesarias.
   *
   * @param value El valor a convertir.
   * @param targetType La clase del tipo al cual se desea convertir el valor.
   * @param <M> El tipo genérico al cual se realiza la conversión.
   * @return El valor convertido al tipo especificado, o `null` si el valor original es `null`.
   * @throws UnexpectedException Si ocurre un error en la conversión o si el tipo no es soportado.
   */
  @SuppressWarnings("unchecked")
  public static <M> M castFieldToType(Object value, Class<M> targetType) {
    try {
      if (value == null) {
        return null;
      } else if (targetType.isInstance(value)) {
        return (M) value;
      } else if (targetType.equals(String.class)) {
        return (M) value.toString();
      } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
        return (M) Integer.valueOf(value.toString());
      } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
        return (M) Long.valueOf(value.toString());
      } else if (targetType.equals(Float.class) || targetType.equals(float.class)) {
        return (M) Float.valueOf(value.toString());
      } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
        return (M) Double.valueOf(value.toString());
      } else if (targetType.equals(BigDecimal.class)) {
        if (value instanceof String string) return (M) new BigDecimal(string);
        if (value instanceof Long l) return (M) BigDecimal.valueOf(l);
        if (value instanceof Double d) return (M) BigDecimal.valueOf(d);
      } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
        if (value instanceof String string) return (M) Boolean.valueOf(string);
      } else if (targetType.equals(OffsetDateTime.class)) {
        if (value instanceof String string) return (M) TimeUtil.convertOffsetDateTime(string);
        if (value instanceof LocalDate date)
          return (M) date.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        if (value instanceof LocalDateTime date)
          return (M) date.atZone(ZoneId.systemDefault()).toOffsetDateTime();
      } else if (targetType.equals(LocalDate.class)) {
        if (value instanceof String string)
          return (M) TimeUtil.convertOffsetDateTime(string).toLocalDate();
        if (value instanceof OffsetDateTime odt) return (M) odt.toLocalDate();
        if (value instanceof LocalDateTime date) return (M) date.toLocalDate();
      } else if (targetType.equals(LocalDateTime.class)) {
        // keep for backwards compat but prefer OffsetDateTime
        if (value instanceof String string)
          return (M) TimeUtil.convertOffsetDateTime(string).toLocalDateTime();
        if (value instanceof LocalDate date) return (M) date.atStartOfDay();
        if (value instanceof OffsetDateTime odt) return (M) odt.toLocalDateTime();
      }
    } catch (Exception e) {
      throw new UnexpectedException(
          "Failed to cast value: {} to type: {}", value, targetType.getName());
    }
    throw new UnexpectedException("Unsupported cast type: {}", targetType.getName());
  }
}
