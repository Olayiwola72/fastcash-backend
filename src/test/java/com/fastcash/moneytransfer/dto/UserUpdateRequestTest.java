package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForCharSequence;
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
class UserUpdateRequestTest {
	
	@Value("${password.regex}") 
	private String passwordPattern;
	
	@Value("${app.admin.password}") 
	private String password;
	
    private final String name = "John Doe";
	
	private Validator validator;
	

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);
        when(constraintValidatorFactory.getInstance(SizeValidatorForCharSequence.class))
                .thenReturn(new SizeValidatorForCharSequence());
        
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
        UserUpdateRequest request = new UserUpdateRequest(name);
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(request);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_WithEmptyOrNullValues() {
        Set<String> expectedPropertyPaths = new HashSet<>(List.of("name"));
        Set<String> actualPropertyPaths = new HashSet<>();

        UserUpdateRequest request = new UserUpdateRequest(null);
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(request);

        for (ConstraintViolation<UserUpdateRequest> violation : violations) {
            actualPropertyPaths.add(violation.getPropertyPath().toString());
        }

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size(), "Expected violations for each null field");
        assertEquals(expectedPropertyPaths, actualPropertyPaths);
    }
    
    
    @Test
    public void testJsonCreator() {
    	 // Create input values
        UserUpdateRequest request = UserUpdateRequest.create(name);
        
        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(name, request.name());
    }
	
}
