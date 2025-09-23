package org.barrikeit.util;

import static org.barrikeit.util.TimeUtil.convertLocalDate;
import static org.barrikeit.util.TimeUtil.convertLocalDateTime;

import jakarta.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.barrikeit.model.domain.GenericEntity;
import org.barrikeit.rest.dto.GenericDto;
import org.barrikeit.util.constants.ExceptionConstants;
import org.barrikeit.util.exceptions.FieldValueException;
import org.barrikeit.util.exceptions.NotFoundException;
import org.barrikeit.util.exceptions.UnExpectedException;
import org.springframework.util.ReflectionUtils;

@Log4j2
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
  @SuppressWarnings("unchecked")
  public static <E> Class<E> getParameterizedTypeClass(Class<E> clazz, int index) {
    ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
    Type[] typeArguments = parameterizedType.getActualTypeArguments();
    return (Class<E>) typeArguments[index];
  }

  /**
   * Resuelve y devuelve la clase del tipo genérico especificado en la superclase del parámetro
   * {@code clazz}.
   *
   * <p>Este método recorre la jerarquía de clases hasta encontrar una superclase parametrizada y
   * devuelve el tipo de argumento genérico en la posición indicada por {@code paramIndex}. Soporta
   * tipos genéricos simples (como {@code DTO}) o colecciones parametrizadas (como {@code
   * List<DTO>}).
   *
   * @param clazz la clase cuya superclase parametrizada se analizará
   * @param paramIndex el índice del parámetro genérico a resolver
   * @return la clase correspondiente al tipo genérico en la posición dada; si es una colección
   *     genérica como {@code Set<DTO>}, devuelve {@code DTO.class}
   * @throws IllegalStateException si no se puede resolver el tipo genérico
   */
  public static Class<?> getSuperClass(Class<?> clazz, int paramIndex) {
    Type type = clazz.getGenericSuperclass();

    while (!(type instanceof ParameterizedType) && clazz.getSuperclass() != null) {
      clazz = clazz.getSuperclass();
      type = clazz.getGenericSuperclass();
    }

    if (type instanceof ParameterizedType parameterizedType) {
      Type actualType = parameterizedType.getActualTypeArguments()[paramIndex];

      // Caso 1: tipo parametrizado (ej. Set<DTO>, List<DTO>, etc.)
      if (actualType instanceof ParameterizedType pt) {
        Type rawType = pt.getRawType();
        if (rawType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {

          Type innerType = pt.getActualTypeArguments()[0];
          if (innerType instanceof Class<?> innerClass) {
            return innerClass;
          }
        }
      }

      // Caso 2: tipo simple directo (ej. DTO)
      if (actualType instanceof Class<?> directClass) {
        return directClass;
      }
    }

    throw new IllegalStateException(
        "No se pudo resolver el tipo genérico en el índice " + paramIndex);
  }

  /**
   * Obtiene todos los campos declarados de una clase, incluyendo los campos de sus superclases.
   *
   * @param clazz Clase de la cual se extraen los campos.
   * @return Una lista con todos los campos declarados de la clase y sus superclases.
   */
  public static List<Field> getFields(Class<?> clazz) {
    List<Field> fields =
        new ArrayList<>(
            Arrays.stream(clazz.getDeclaredFields())
                .filter(
                    field ->
                        !field.isAnnotationPresent(Transient.class)
                            && !Modifier.isTransient(field.getModifiers())
                            && !Modifier.isStatic(field.getModifiers()))
                .toList());

    Class<?> superClass = clazz.getSuperclass();
    if (superClass != null) {
      fields.addAll(getFields(superClass));
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
   */
  public static List<Field> getFieldsWithAnnotation(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    return getFields(clazz).stream()
        .filter(field -> field.isAnnotationPresent(annotation))
        .toList();
  }

  /**
   * Obtiene todos los campos de una clase, incluyendo los campos de sus superclases, que NO estén
   * anotados con una anotación específica.
   *
   * @param clazz Clase de la cual se extraen los campos NO anotados.
   * @param annotation La clase de la anotación que se busca en los campos.
   * @return Una lista con todos los campos SIN anotar de la clase y sus superclases.
   */
  public static List<Field> getFieldsWithoutAnnotation(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    return getFields(clazz).stream()
        .filter(field -> !field.isAnnotationPresent(annotation))
        .toList();
  }

  /**
   * Obtiene los campos de una clase, reflejando la estructura jerárquica en los nombres.
   *
   * @param clazz Clase de la cual se extraen los campos.
   * @param prefix Nombre del campo padre, en el caso de usar la funcion de manera recursiva permite
   *     representar nombres completos de la estructura jerarquica en formato "padre.hijo".
   * @return Un `Map` que asocia el nombre completo de cada campo con el objeto `Field`. Los nombres
   *     reflejan la estructura jerárquica como "campo1.campo2".
   */
  public static Map<String, Field> getNestedFields(Class<?> clazz, String prefix) {
    return getFields(clazz).stream()
        .flatMap(
            field -> {
              String fullFieldName = buildFullFieldName(prefix, field.getName());
              if (isEntityOrDto(field.getDeclaringClass())) {
                return getNestedFields(field.getType(), fullFieldName).entrySet().stream();
              } else {
                return Stream.of(Map.entry(fullFieldName, field));
              }
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Obtiene de una clase que esté anotada con una anotación específica los valores de las
   * propiedades de la anotación y devuelve un mapa con el nombre del campo y el objeto `Field`.
   *
   * @param clazz Clase de la cual se extraen las propiedades de la anotación.
   * @param annotation La clase de la anotación.
   * @return Un `Map` donde las claves son los nombres de las propiedades y los valores son los
   *     valores de las propiedades.
   * @throws NotFoundException Si no se encuentra ningún campo con la anotación especificada.
   */
  public static Map<String, Object> getAnnotationProperties(
      Class<?> clazz, Class<? extends Annotation> annotation) {
    Map<String, Object> annotationParams = new HashMap<>();
    if (clazz.isAnnotationPresent(annotation)) {
      Annotation annotationInstance = clazz.getAnnotation(annotation);
      for (Method method : annotation.getDeclaredMethods()) {
        try {
          Object value = MethodUtils.invokeMethod(annotationInstance, method.getName());
          annotationParams.put(method.getName(), value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
          throw new FieldValueException(
              ExceptionConstants.ERROR_FIELD_GET_VALUE, method.getName(), annotationInstance);
        }
      }
      return annotationParams;
    } else {
      throw new NotFoundException(ExceptionConstants.NOT_FOUND, annotation, clazz);
    }
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
              if (isEntityOrDto(field.getDeclaringClass())) {
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
  // TODO: problema con las instancias
  //  Si se trata de un field nesteado la instancia es la isntancia raiz
  //  La instancia es User que tiene String nombre y Direccion direccion
  //  y Direccion tiene String calle, Numero numero y String piso
  //  y Numero tiene String numero y String portal
  //  Siendo en el Map:
  //    "nombre" -> {Field@1234} String
  //    "direccion.calle" -> {Field@1241} String
  //    "direccion.numero" -> {Field@1242} String
  //    "direccion.numero.numero" -> {Field@1251} String
  //    "direccion.numero.portal" -> {Field@1252} String
  //    "direccion.piso" -> {Field@1243} String
  //  si la instancia que llega es user solo se va a obtener el nombre
  //  xq siendo la instancia User solo es capaz de hacer el getFieldValue de nombre y direccion
  //  luego del valor de direccion se podra hacer los getFieldValue correspondientes
  public static Map<String, Object> getMapFieldValues(Object instance, Map<String, Field> fields) {
    Map<String, Object> values = new LinkedHashMap<>();

    for (Map.Entry<String, Field> entry : fields.entrySet()) {
      String fieldName = entry.getKey();
      Field field = entry.getValue();
      Object value = getFieldValue(instance, field.getName());

      if (value == null) {
        values.put(fieldName, null);
        continue;
      }

      Class<?> type = value.getClass();
      if (isEntityOrDto(type)) {
        Map<String, Field> nestedFields = getNestedFields(value.getClass(), fieldName);
        values.putAll(getMapFieldValues(value, nestedFields));
      } else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
        Collection<?> collection =
            type.isArray() ? Arrays.asList((Object[]) value) : (Collection<?>) value;
        int index = 0;
        for (Object item : collection) {
          if (item == null) continue;
          String indexedPrefix = fieldName + ".[" + index + "]";
          if (isSimpleType(item.getClass())) {
            values.put(indexedPrefix, item);
          } else if (isEntityOrDto(item.getClass())) {
            Map<String, Field> nestedFields = getNestedFields(item.getClass(), indexedPrefix);
            values.putAll(getMapFieldValues(item, nestedFields));
          } else {
            throw new NotFoundException(
                "Nested collection inside collection not allowed: " + indexedPrefix);
          }
          index++;
        }

      } else if (Map.class.isAssignableFrom(type)) {
        Map<?, ?> map = (Map<?, ?>) value;
        for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
          String key = String.valueOf(mapEntry.getKey());
          Object val = mapEntry.getValue();
          String mappedPrefix = fieldName + ".[" + key + "]";
          if (val == null) continue;
          if (isSimpleType(val.getClass())) {
            values.put(mappedPrefix, val);
          } else if (isEntityOrDto(val.getClass())) {
            Map<String, Field> nestedFields = getNestedFields(val.getClass(), mappedPrefix);
            values.putAll(getMapFieldValues(val, nestedFields));
          } else {
            throw new NotFoundException("Nested map inside map not allowed: " + mappedPrefix);
          }
        }

      } else {
        values.put(fieldName, value);
      }
    }

    return values;
  }

  /**
   * Obtiene los valores de los campos anidados de una clase, reflejando la estructura jerárquica en
   * los nombres.
   *
   * @param instance Instancia de una clase de la cual se extraen los valores de los campos
   *     anidados.
   * @param prefix Nombre del campo padre, usado recursivamente para construir nombres completos en
   *     formato "padre.hijo".
   * @return Un `Map` que asocia el nombre completo de cada campo con el objeto `Field`. Los nombres
   *     reflejan la estructura jerárquica como "campo1.campo2".
   */
  public static Map<String, Object> getNestedFieldValues(Object instance, String prefix) {
    Map<String, Object> values = new HashMap<>();

    for (Field field : getFields(instance.getClass())) {
      Object value = getFieldValue(instance, field.getName());
      String fieldName = (prefix == null ? field.getName() : prefix + "." + field.getName());

      if (value == null) {
        values.put(fieldName, null);
        continue;
      }
      Class<?> type = value.getClass();
      if (GenericEntity.class.isAssignableFrom(type) || GenericDto.class.isAssignableFrom(type)) {
        values.putAll(getNestedFieldValues(value, fieldName));
      } else if (type.isArray() || Collection.class.isAssignableFrom(type)) {
        Collection<?> collection =
            type.isArray() ? Arrays.asList((Object[]) value) : (Collection<?>) value;
        if (collection.isEmpty()) values.put(fieldName, null);
        else {
          int index = 0;
          for (Object item : collection) {
            String indexedPrefix = fieldName + ".[" + index + "]";
            if (isSimpleType(item.getClass())) {
              values.put(indexedPrefix, item);
            } else {
              values.putAll(getNestedFieldValues(item, indexedPrefix));
            }
            index++;
          }
        }
      } else if (Map.class.isAssignableFrom(type)) {
        Map<?, ?> map = (Map<?, ?>) value;
        if (map.isEmpty()) values.put(fieldName, null);
        for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
          String key = String.valueOf(mapEntry.getKey());
          Object item = mapEntry.getValue();
          String mappedPrefix = fieldName + ".[" + key + "]";
          if (isSimpleType(item.getClass())) {
            values.put(mappedPrefix, item);
          } else {
            values.putAll(getNestedFieldValues(item, mappedPrefix));
          }
        }

      } else {
        values.put(fieldName, value);
      }
    }
    return values;
  }

  private static String buildFullFieldName(String parent, String child) {
    return (parent == null || parent.isBlank()) ? child : parent + "." + child;
  }

  private static boolean isEntityOrDto(Class<?> clazz) {
    return GenericEntity.class.isAssignableFrom(clazz) || GenericDto.class.isAssignableFrom(clazz);
  }

  private static boolean isSimpleType(Class<?> type) {
    return type.isPrimitive()
        || type.isEnum()
        || type.equals(String.class)
        || Number.class.isAssignableFrom(type)
        || Boolean.class.isAssignableFrom(type)
        || Date.class.isAssignableFrom(type)
        || type.equals(LocalDate.class)
        || type.equals(LocalDateTime.class);
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
