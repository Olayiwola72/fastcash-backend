package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserTypeCheckerExceptionTest {
	
	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "A Google account with this email already exists. Please continue with Google, or set up your password from your profile settings";
        
        // Act
        UserTypeCheckerException exception = new UserTypeCheckerException(message);
        
        // Assert
        assertEquals(message, exception.getMessage());
    }
	
}