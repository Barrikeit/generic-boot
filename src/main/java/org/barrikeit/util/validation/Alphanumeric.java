package org.barrikeit.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = AlphanumericValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Alphanumeric {
  String message() default "El campo debe ser alfanumérico.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
