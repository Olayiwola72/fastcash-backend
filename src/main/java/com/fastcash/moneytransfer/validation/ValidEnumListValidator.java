package com.fastcash.moneytransfer.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fastcash.moneytransfer.annotation.ValidEnumList;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEnumListValidator implements ConstraintValidator<ValidEnumList, List<?>> {
    private Class<? extends Enum<?>> enumClass;
    private int maxAllowedSize;

    @Override
    public void initialize(ValidEnumList constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        maxAllowedSize = enumClass.getEnumConstants().length;
    }

    @Override
    public boolean isValid(List<?> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty()) {
            return true; // Let other validators handle Null value
        }
        
        // Validate the size
        if (values.size() > maxAllowedSize) {
            return false;
        }
        
        for (Object value : values) {
            if (!isValidEnumValue(value)) {
                return false; // Invalid enum value found
            }
        }
        
        // Validate duplicate values
        Set<?> uniqueValues = new HashSet<>(values);
        if (uniqueValues.size() < values.size()) {
            return false; // Duplicate values found
        }
        
        return true; // All values are valid enums
    }

    private boolean isValidEnumValue(Object value) {
        if (value == null) {
            return false; // Null value is not a valid enum value
        }
        
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumValue -> enumValue.name().equals(value.toString()));
    }
}
