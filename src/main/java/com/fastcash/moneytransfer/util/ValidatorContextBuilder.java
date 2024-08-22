package com.fastcash.moneytransfer.util;

import jakarta.validation.ConstraintValidatorContext;

public class ValidatorContextBuilder {
	public static void buildConstraintViolation(ConstraintValidatorContext context, String propertyNode) {
		ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
        
    	if (violationBuilder != null) {
            violationBuilder.addPropertyNode(propertyNode) // Specify the field associated with the error
            	.addConstraintViolation(); // Add the violation
        }
	}
}
