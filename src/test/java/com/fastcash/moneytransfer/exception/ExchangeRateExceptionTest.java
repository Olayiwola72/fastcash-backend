package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ExchangeRateExceptionTest {
	
	private final String message = "Failed to retrieve exchange data";
	private ExchangeRateException exception;

    @Test
    void testMessageConstructor() {
    	//Act
    	exception = new ExchangeRateException(message);
    	
        // Assert
        assertNotNull(exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    void testMessageAndCauseConstructor() {
    	//Act
    	exception = new ExchangeRateException(message, new Exception());
    	
        // Assert
        assertNotNull(exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
}
