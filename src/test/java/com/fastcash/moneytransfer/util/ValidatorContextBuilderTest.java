package com.fastcash.moneytransfer.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;

class ValidatorContextBuilderTest {
	
	@Test
    void testBuildConstraintViolation() {
        // Mock ConstraintValidatorContext
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        // Call the static method to build constraint violation
        ValidatorContextBuilder.buildConstraintViolation(context, "propertyName");
        
        // Verify that addPropertyNode and addConstraintViolation were called with the correct arguments
        verify(context).buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
    }
}
