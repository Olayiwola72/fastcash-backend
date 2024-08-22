package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
class AccountRequestTest {
	
	@Value("${password.regex}") 
	private String passwordPattern;
	
	@Value("${app.admin.password}") 
	private String password;
	
	private Validator validator;
	
    private Boolean allowOverdraft = true;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the ConstraintValidatorFactory to provide PasswordValidator with desired constructor argument
        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);
        when(constraintValidatorFactory.getInstance(NotNullValidator.class))
        	.thenReturn(new NotNullValidator());
        
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(constraintValidatorFactory)
                .buildValidatorFactory();

        validator = validatorFactory.getValidator();
    }

    @Test
    public void testValidRequest() {
        AccountRequest request = new AccountRequest(allowOverdraft);
        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidRequest_NullValues() {
    	AccountRequest request = new AccountRequest(null);
        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<AccountRequest> violation : violations) {
        	assertEquals("allowOverdraft", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testJsonCreator() {
        AccountRequest request = AccountRequest.create(allowOverdraft);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(allowOverdraft, request.allowOverdraft());
    }
	
}
