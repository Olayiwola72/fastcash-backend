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
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.fastcash.moneytransfer.validation.PasswordValidator;
import com.fastcash.moneytransfer.validation.ValidEnumListValidator;
import com.fastcash.moneytransfer.validation.ValidEnumValidator;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
class UserRequestTest {
	
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
        
        when(constraintValidatorFactory.getInstance(NotNullValidator.class))
        	.thenReturn(new NotNullValidator());
        
        when(constraintValidatorFactory.getInstance(ValidEnumValidator.class))
        	.thenReturn(new ValidEnumValidator());
        
        when(constraintValidatorFactory.getInstance(ValidEnumListValidator.class))
        	.thenReturn(new ValidEnumListValidator());
        
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
    	 // Create input values
        String email = "test@email.com";
        
        UserRequest request = new UserRequest(email, password);
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmptyEmail() {
        UserRequest request = new UserRequest("", password);
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<UserRequest> violation : violations) {
        	assertEquals("email", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testInvalidEmail() {
        UserRequest request = new UserRequest("test", password);
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<UserRequest> violation : violations) {
        	assertEquals("email", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testEmptyPasword() {
        UserRequest request = new UserRequest("test@moneytransfer.com", "");
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<UserRequest> violation : violations) {
        	assertEquals("password", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testInvalidPassword() {
        UserRequest request = new UserRequest("test@moneytransfer.com", "1234567756");
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<UserRequest> violation : violations) {
        	assertEquals("password", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testJsonCreator() {
    	 // Create input values
        String email = "test@email.com";
        
        UserRequest request = UserRequest.create(email, password);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(email, request.email());
        assertEquals(password, request.password());
    }
	
}
