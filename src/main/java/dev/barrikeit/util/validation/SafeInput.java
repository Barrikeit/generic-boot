package dev.barrikeit.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SafeInputValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeInput {

    String message() default "El campo contiene caracteres no permitidos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}