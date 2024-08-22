package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserAccountMismatchExceptionTest {
	
	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "User account mismatch";
        String code = "UserAccountMismatch";
        Object[] values = {"value1", "value2"};
        String fieldName = "disabledField";
        
        // Act
        UserAccountMismatchException exception = new UserAccountMismatchException(message, code, values, fieldName);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
        assertEquals(values, exception.getValues());
        assertEquals(fieldName, exception.getFieldName());
    }
	
}
