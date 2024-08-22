package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UsernameAlreadyExistsExceptionTest {
	
	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "Username already exists";
        String code = "UsernameAlreadyExists";
        Object[] values = {"value1", "value2"};
        String fieldName = "disabledField";
        
        // Act
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(message, code, values, fieldName);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
        assertEquals(values, exception.getValues());
        assertEquals(fieldName, exception.getFieldName());
    }
	
}
