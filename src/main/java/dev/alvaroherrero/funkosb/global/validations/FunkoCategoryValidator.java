package dev.alvaroherrero.funkosb.global.validations;

import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.global.validations.validanotations.ValidCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FunkoCategoryValidator implements ConstraintValidator<ValidCategory, FunkoCategory> {
    @Override
    public boolean isValid(FunkoCategory value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value == FunkoCategory.DISNEY ||
                value == FunkoCategory.SERIE ||
                value == FunkoCategory.PELICULA ||
                value == FunkoCategory.SUPERHEROES ||
                value == FunkoCategory.OTROS;
    }
}
