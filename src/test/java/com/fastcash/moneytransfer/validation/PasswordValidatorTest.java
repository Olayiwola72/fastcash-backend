package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.validation.ConstraintValidatorContext;

@DataJpaTest
class PasswordValidatorTest {
	
	@Value("${password.regex}")
	private String passwordPattern;
	
	@Value("${app.admin.password}") 
	private String adminPassword;

    @Mock
    private ConstraintValidatorContext context;
    
    private PasswordValidator validator;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new PasswordValidator(passwordPattern);
    }

    @Test
    void testIsValid_WithValidPassword() {
        // Act
        boolean isValid = validator.isValid(adminPassword, context);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsValid_WithInvalidPassword() {
        // Act
        boolean isValid = validator.isValid("123456", context);

        // Assert
        assertFalse(isValid);
    }
    
}
