package org.barrikeit.util;

import static org.barrikeit.util.TimeUtil.convertLocalDate;
import static org.barrikeit.util.TimeUtil.convertLocalDateTime;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.service.dto.GenericDto;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.FieldValueException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.barrikeit.util.exceptions.UnExpectedException;
import org.springframework.util.ReflectionUtils;

public class ReflectionUtil extends ReflectionUtils {
  private ReflectionUtil() {
    throw new IllegalStateException("ReflectionUtil class");
  }

  /**
   * Crea una nueva instancia de una clase utilizando su constructor sin argumentos.
   *
   * @param clazz La clase de la cual se desea crear una nueva instancia.
   * @return Una nueva instancia de la clase especificada o `null` si ocurre un error al crearla
   *     (por ejemplo, si no tiene un constructor sin argumentos o si este no es accesible).
   */
  public static Object newInstance(Class<?> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      return null;
    }
  }

  /**
   * Obtiene el valor de un campo especificado de una instancia utilizando su método getter.
   *
   * @param instance La instancia de la cual se desea obtener el valor del campo.
   * @param fieldName El nombre del campo cuyo valor se desea obtener.
   * @return El valor del campo especificado o `null` si no se encuentra el método getter o si el
   *     campo es inaccesible.
   * @throws FieldValueException Si ocurre un error al intentar invocar el getter o al acceder al
   *     valor.
   */
  public static Object getFieldValue(final Object instance, String fieldName) {
    Object value = null;
    try {
      String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      Method getterMethod = findMethod(instance.getClass(), getterName);
      if (getterMethod != null) {
        value = getterMethod.invoke(instance);
      }
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
      throw new FieldValueException(ExceptionConstants.ERROR_FIELD_GET_VALUE, fieldName, instance);
    }
    return value;
  }

  /**
   * Establece un valor en un campo de una instancia utilizando su método setter.
   *
   * @param instance La instancia en la cual se desea establecer el valor del campo.
   * @param fieldName El nombre del campo al cual se le asignará el valor.
   * @param value El valor a establecer en el campo.
   * @throws FieldValueException Si ocurre un error al intentar invocar el setter o al asignar el
   *     valor.
   */
  public static void setFieldValue(final Object instance, String fieldName, Object value) {
    try {
      String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      Method setterMethod = findMethod(instance.getClass(), setterName, value.getClass());
      if (setterMethod != null) {
        setterMethod.invoke(instance, value);
      }
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
      throw new FieldValueException(ExceptionConstants.ERROR_FIELD_SET_VALUE, fieldName, instance);
    }
  }

  /**
   * Obtiene la clase del tipo genérico parametrizado en el índice especificado de una clase
   * genérica.
   *
   * @param clazz Clase de la cual se extraerá el tipo parametrizado.
   * @param index Índice del parámetro genérico dentro de la clase.
   * @return La clase correspondiente al tipo genérico parametrizado.
   * @throws ClassCastException Si no se puede convertir el tipo genérico al tipo esperado.
   */
  public static <E> Class<E> getParameterizedTypeClass(Class<E> clazz, int index) {
    ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
    Type[] typeArguments = parameterizedType.getActualTypeArguments();
    @SuppressWarnings("unchecked")
    Class<E> tClass = (Class<E>) typeArguments[index];
    return tClass;
  }

  /**
   * Obtiene todos los campos declarados de una clase, incluyendo los campos de sus superclases.
   *
   * @param clazz Clase de la cual se extraen los campos.
   * @return Una lista con todos los campos declarados de la clase y sus superclases.
   */
  public static List<Field> getFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    while (clazz != null) {
      fields.addAll(List.of(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  /**
   * Obtiene todos los campos de una clase, incluyendo los campos de sus superclases, que estén
   * anotados con una anotación específica.
   *
   * @param clazz Clase de la cual se extraen los campos anotados.
   * @param annotation La clase de la anotación que se busca en los campos.
   * @return Una lista con todos los campos anotados de la clase y sus superclases.
   * @throws NotFoundException Si no se encuentra ningún campo con la anotación especificada.
   */
  public static List<Field> getFieldsWithAnnotation(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    List<Field> annotatedFields =
        getFields(clazz).stream().filter(field -> field.isAnnotationPresent(annotation)).toList();
    if (annotatedFields.isEmpty())
      throw new NotFoundException(ExceptionConstants.ERROR_MISSING_ANNOTATION, annotation, clazz);
    return annotatedFields;
  }

  /**
   * Obtiene los campos anidados de una clase, reflejando la estructura jerárquica en los nombres.
   *
   * @param clazz Clase de la cual se extraen los campos anidados.
   * @param fieldName Nombre del campo padre, usado recursivamente para construir nombres completos
   *     en formato "padre.hijo".
   * @return Un `Map` que asocia el nombre completo de cada campo con el objeto `Field`. Los nombres
   *     reflejan la estructura jerárquica como "campo1.campo2".
   */
  public static Map<String, Field> getNestedFields(Class<?> clazz, String fieldName) {
    return getFields(clazz).stream()
        .flatMap(
            field -> {
              String fullFieldName = buildFullFieldName(fieldName, field.getName());
              if (GenericEntity.class.isAssignableFrom(field.getDeclaringClass())
                  || GenericDto.class.isAssignableFrom(field.getDeclaringClass())) {
                return getNestedFields(field.getType(), fullFieldName).entrySet().stream();
              } else {
                return Stream.of(Map.entry(fullFieldName, field));
              }
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Obtiene todos los campos de una clase, incluyendo los de sus superclases, que estén anotados
   * con una anotación específica y devuelve un mapa con el nombre del campo y el objeto `Field`.
   *
   * @param clazz Clase de la cual se extraen los campos anotados.
   * @param annotation La clase de la anotación que se busca en los campos.
   * @return Un `Map` donde las claves son los nombres de los campos y los valores son los objetos
   *     `Field`.
   * @throws NotFoundException Si no se encuentra ningún campo con la anotación especificada.
   */
  public static Map<String, Field> getAnnotatedFields(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    return getFieldsWithAnnotation(clazz, annotation).stream()
        .map(field -> Map.entry(field.getName(), field))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Obtiene todos los campos anidados de una clase que estén anotados con una anotación específica.
   * Los nombres de los campos reflejan la estructura jerárquica en formato "padre.hijo".
   *
   * @param clazz Clase de la cual se extraen los campos anotados.
   * @param annotation La clase de la anotación que se busca en los campos.
   * @param fieldName Nombre del campo padre, usado recursivamente para construir nombres completos.
   * @return Un `Map` que asocia el nombre completo de cada campo anotado con el objeto `Field`.
   * @throws NotFoundException Si no se encuentra ningún campo con la anotación especificada.
   */
  public static Map<String, Field> getAnnotatedNestedFields(
      Class<?> clazz, Class<? extends Annotation> annotation, String fieldName) {
    return getFieldsWithAnnotation(clazz, annotation).stream()
        .flatMap(
            field -> {
              String fullFieldName = buildFullFieldName(fieldName, field.getName());
              if (GenericEntity.class.isAssignableFrom(field.getDeclaringClass())
                  || GenericDto.class.isAssignableFrom(field.getDeclaringClass())) {
                return getAnnotatedNestedFields(
                    field.getDeclaringClass(), annotation, fullFieldName)
                    .entrySet()
                    .stream();
              } else {
                return Stream.of(Map.entry(fullFieldName, field));
              }
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Obtiene los valores de una lista de campos de una instancia.
   *
   * @param instance La instancia de la cual se obtendrán los valores de los campos.
   * @param fields Lista de campos cuyos valores se desean obtener.
   * @return Una lista con los valores de los campos especificados.
   */
  public static List<Object> getListFieldValues(Object instance, List<Field> fields) {
    List<Object> values = new ArrayList<>();
    for (Field field : fields) {
      values.add(getFieldValue(instance, field.getName()));
    }
    return values;
  }

  /**
   * Obtiene los valores de un conjunto de campos de una instancia, organizados en un mapa.
   *
   * @param instance La instancia de la cual se obtendrán los valores de los campos.
   * @param fields Un mapa donde las claves son los nombres de los campos y los valores son los
   *     objetos `Field`.
   * @return Un `Map` que asocia cada nombre de campo con su valor correspondiente.
   */
  public static Map<String, Object> getMapFieldValues(Object instance, Map<String, Field> fields) {
    Map<String, Object> values = new HashMap<>();
    for (Map.Entry<String, Field> entry : fields.entrySet()) {
      values.put(entry.getKey(), getFieldValue(instance, entry.getValue().getName()));
    }
    return values;
  }

  private static String buildFullFieldName(String parent, String child) {
    return (parent == null) ? child : parent + "." + child;
  }

  /**
   * Convierte un valor al tipo especificado, realizando las transformaciones necesarias.
   *
   * @param value El valor a convertir.
   * @param targetType La clase del tipo al cual se desea convertir el valor.
   * @param <M> El tipo genérico al cual se realiza la conversión.
   * @return El valor convertido al tipo especificado, o `null` si el valor original es `null`.
   * @throws UnExpectedException Si ocurre un error en la conversión o si el tipo no es soportado.
   */
  @SuppressWarnings("unchecked")
  private <M> M castFieldToType(Object value, Class<M> targetType) {
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
      } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
        if (value instanceof String string) return (M) Boolean.valueOf(string);
      } else if (targetType.equals(LocalDate.class)) {
        if (value instanceof String string) return (M) convertLocalDate(string);
        if (value instanceof LocalDateTime date) return (M) date.toLocalDate();
      } else if (targetType.equals(LocalDateTime.class)) {
        if (value instanceof String string) return (M) convertLocalDateTime(string);
        if (value instanceof LocalDate date) return (M) date.atStartOfDay();
      }
    } catch (Exception e) {
      throw new UnExpectedException(
          "Failed to cast value: {} to type: {}", value, targetType.getName());
    }
    throw new UnExpectedException("Unsupported cast type: {}", targetType.getName());
  }
}
