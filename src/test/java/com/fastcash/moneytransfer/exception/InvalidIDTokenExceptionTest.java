package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvalidIDTokenExceptionTest {

	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "Invalid ID token.";
        
        // Act
        InvalidIDTokenException exception = new InvalidIDTokenException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
    }

}
