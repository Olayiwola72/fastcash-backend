package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenRefreshExceptionTest {
	
	private TokenRefreshException exception;
	
	@BeforeEach
	void setUp() {
		String token = "sampleToken";
        String message = "Token is expired";
        
        // Act
        exception = new TokenRefreshException(token, message);
	}

    @Test
    void testTokenRefreshExceptionMessage() {
        // Assert
        String expectedMessage = "Failed for [sampleToken]: Token is expired";
        assertEquals(expectedMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}
