package dev.alvaroherrero.funkosb.validations;

import dev.alvaroherrero.funkosb.models.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.validations.validanotations.ValidCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FunkoCategoryValidator implements ConstraintValidator<ValidCategory, FunkoCategory> {
    @Override
    public boolean isValid(FunkoCategory value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value == FunkoCategory.ACTION ||
                value == FunkoCategory.COMEDY ||
                value == FunkoCategory.DRAMA ||
                value == FunkoCategory.FANTASY ||
                value == FunkoCategory.HORROR ||
                value == FunkoCategory.MYSTERY ||
                value == FunkoCategory.THRILLER;
    }
}
