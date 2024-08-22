package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.annotation.ValidEnumList;
import com.fastcash.moneytransfer.enums.Currency;

import jakarta.validation.Payload;

class ValidEnumListValidatorTest {
	private ValidEnumListValidator validator;
	
    // Define your enum for testing
    enum TestEnum {
        VALUE1, VALUE2, VALUE3
    }

	@BeforeEach
    void setUp() {
		 ValidEnumList annotation = new ValidEnumList() {
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
                return ValidEnumList.class;
            }

			@Override
			public String message() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class<? extends Payload>[] payload() {
				// TODO Auto-generated method stub
				return null;
			}
        };
	        
        validator = new ValidEnumListValidator();
        validator.initialize(annotation);
    }
    
    @Test
    void testEmptyList() {
    	assertTrue(validator.isValid(new ArrayList<>(), null));
    }

    @Test
    void testValidEnumList() {
        List<TestEnum> validList = Arrays.asList(TestEnum.VALUE1, TestEnum.VALUE2);
        assertTrue(validator.isValid(validList, null));
    }
    
    @Test
    void testInvalidEnumList() {
        List<?> invalidList = Arrays.asList(TestEnum.VALUE1, TestEnum.VALUE2, Currency.NGN);
        assertFalse(validator.isValid(invalidList, null));
    }
    
    @Test
    void testInvalidEnumListSize() {
        List<TestEnum> validList = Arrays.asList(TestEnum.VALUE1, TestEnum.VALUE2, TestEnum.VALUE3, TestEnum.VALUE1);
        assertFalse(validator.isValid(validList, null));
    }

    @Test
    void testNullList() {
    	assertTrue(validator.isValid(null, null));
    }

    @Test
    void testListWithDuplicateValues() {
        List<TestEnum> listWithDuplicates = Arrays.asList(TestEnum.VALUE1, TestEnum.VALUE1, TestEnum.VALUE2);
        assertFalse(validator.isValid(listWithDuplicates, null));
    }
    
}
