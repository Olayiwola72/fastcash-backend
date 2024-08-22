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
class ResetPasswordRequestTest {
	
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
    	ResetPasswordRequest request = new ResetPasswordRequest("token", password);
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testBlankPasword() {
    	ResetPasswordRequest request = new ResetPasswordRequest(null, null);
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }
    
    @Test
    public void testInvalidPassword() {
    	ResetPasswordRequest request = new ResetPasswordRequest("token","1234567756");
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<ResetPasswordRequest> violation : violations) {
        	assertEquals("password", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testJsonCreator() {
    	 // Create input values
    	ResetPasswordRequest request = ResetPasswordRequest.create("token", password);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(password, request.password());
    }
	
}
