package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.annotation.ValidEnum;

import jakarta.validation.Payload;

class ValidEnumValidatorTest {
	private ValidEnumValidator validator;
	
    // Define your enum for testing
    enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }

	@BeforeEach
    void setUp() {
		 ValidEnum annotation = new ValidEnum() {
            @Override
            public Class<? extends Enum<?>> enumClass() {
                return TestEnum.class;
            }

            @Override
            public Class<?>[] groups() {
                return new Class<?>[0];
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ValidEnum.class;
            }

			@Override
			public String message() {
				return null;
			}

			@Override
			public Class<? extends Payload>[] payload() {
				return null;
			}
        };
	        
        validator = new ValidEnumValidator();
        validator.initialize(annotation);
    }
    
    @Test
    void testEmptyList() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testValidEnum() {
        assertTrue(validator.isValid("VALUE1", null));
    }
    
    @Test
    void testInvalidEnum() {
        assertFalse(validator.isValid("NGN", null));
    }
    
}
