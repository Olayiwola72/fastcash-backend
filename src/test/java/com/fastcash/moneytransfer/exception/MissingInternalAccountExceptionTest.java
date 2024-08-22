package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MissingInternalAccountExceptionTest {
	
	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "Internal account is missing";
        String code = "MissingInternalAccount";
        Object[] values = {"value1", "value2"};
        String fieldName = "disabledField";
        
        // Act
        MissingInternalAccountException exception = new MissingInternalAccountException(message, code, values, fieldName);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
        assertEquals(values, exception.getValues());
        assertEquals(fieldName, exception.getFieldName());
    }
	
}
