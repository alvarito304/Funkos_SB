package dev.alvaroherrero.funkosb.global.validations.validanotations;

import dev.alvaroherrero.funkosb.global.validations.FunkoCategoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FunkoCategoryValidator.class)
public @interface ValidCategory {
    String message() default "Introduzca una categoria definida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] value() default "ACTION";  // The category value to be validated.
}
