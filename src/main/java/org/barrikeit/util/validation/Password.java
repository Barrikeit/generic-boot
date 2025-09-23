package org.barrikeit.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

  String message() default
      "La contraseña no es válida, tiene que tener mínimo 8 caracteres, no tendrá  espacios, tiene que tener  MAYÚSCULAS, minúsculas, "
          + "números del 0-9 y alguno de los siguientes caracteres @#$%^&+=";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
