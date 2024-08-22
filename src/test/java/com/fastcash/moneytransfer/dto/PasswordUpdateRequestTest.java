package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.fastcash.moneytransfer.validation.PasswordValidator;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
class PasswordUpdateRequestTest {
	
	@Value("${password.regex}") 
	private String passwordPattern;
	
	@Value("${app.admin.password}") 
	private String password;
	
	private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the ConstraintValidatorFactory to provide PasswordValidator with desired constructor argument
        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);
        PasswordValidator passwordValidator = new PasswordValidator(passwordPattern);
        when(constraintValidatorFactory.getInstance(PasswordValidator.class))
                .thenReturn(passwordValidator);
        
        when(constraintValidatorFactory.getInstance(NotBlankValidator.class))
        	.thenReturn(new NotBlankValidator());
        
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(constraintValidatorFactory)
                .buildValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @Test
    public void testValidRequest() {
    	 // Create input values
    	PasswordUpdateRequest request = new PasswordUpdateRequest(password);
        Set<ConstraintViolation<PasswordUpdateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testBlankPasword() {
    	PasswordUpdateRequest request = new PasswordUpdateRequest(null);
        Set<ConstraintViolation<PasswordUpdateRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<PasswordUpdateRequest> violation : violations) {
        	assertEquals("password", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testInvalidPassword() {
    	PasswordUpdateRequest request = new PasswordUpdateRequest("1234567756");
        Set<ConstraintViolation<PasswordUpdateRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<PasswordUpdateRequest> violation : violations) {
        	assertEquals("password", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testJsonCreator() {
    	 // Create input values
        PasswordUpdateRequest request = PasswordUpdateRequest.create(password);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(password, request.password());
    }
	
}
