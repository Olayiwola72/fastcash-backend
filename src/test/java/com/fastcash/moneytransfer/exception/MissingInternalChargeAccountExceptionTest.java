package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MissingInternalChargeAccountExceptionTest {
	
	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "Internal Charge UserAccount is missing";
        String code = "MissingInternalChargeAccount";
        Object[] values = {"value1", "value2"};
        String fieldName = "disabledField";
        
        // Act
        MissingInternalChargeAccountException exception = new MissingInternalChargeAccountException(message, code, values, fieldName);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
        assertEquals(values, exception.getValues());
        assertEquals(fieldName, exception.getFieldName());
    }
	
}
