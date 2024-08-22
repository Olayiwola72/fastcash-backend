package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
class ForgotPasswordRequestTest {

	private Validator validator;
	
    private final String email = "test@email.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the ConstraintValidatorFactory to provide PasswordValidator with desired constructor argument
        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);

        when(constraintValidatorFactory.getInstance(NotBlankValidator.class))
        	.thenReturn(new NotBlankValidator());
        
        when(constraintValidatorFactory.getInstance(EmailValidator.class))
        	.thenReturn(new EmailValidator());
        
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(constraintValidatorFactory)
                .buildValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @Test
    public void testValidRequest() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmptyEmail() {
    	ForgotPasswordRequest request = new ForgotPasswordRequest("");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<ForgotPasswordRequest> violation : violations) {
        	assertEquals("email", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testInvalidEmail() {
    	ForgotPasswordRequest request = new ForgotPasswordRequest("test");
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<ForgotPasswordRequest> violation : violations) {
        	assertEquals("email", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testJsonCreator() {
        ForgotPasswordRequest request = ForgotPasswordRequest.create(email);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(email, request.email());
    }
	
}
