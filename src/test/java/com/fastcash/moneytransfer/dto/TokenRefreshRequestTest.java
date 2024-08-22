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
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
class TokenRefreshRequestTest {
	
	private final String refreshToken = "refreshToken";
	
	private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);
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
    	TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        Set<ConstraintViolation<TokenRefreshRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testBlankInput() {
    	TokenRefreshRequest request = new TokenRefreshRequest(null);
        Set<ConstraintViolation<TokenRefreshRequest>> violations = validator.validate(request);
        
        for(ConstraintViolation<TokenRefreshRequest> violation : violations) {
        	assertEquals("refreshToken", violation.getPropertyPath().toString());
        }
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
    }
    
    @Test
    public void testJsonCreator() {
    	 // Create input values
    	TokenRefreshRequest request = TokenRefreshRequest.create(refreshToken);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(refreshToken, request.refreshToken());
    }
	
}
