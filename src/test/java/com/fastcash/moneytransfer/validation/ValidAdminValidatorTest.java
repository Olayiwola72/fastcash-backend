package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.Admin;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ValidAdminValidatorTest {
	
	private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAdminWithInternalUserType_thenValidationSucceeds() {
    	Admin admin = new Admin();
		admin.setEmail("test@emial.com");
		admin.setPassword("Password1$");
		admin.setRoles("ADMIN");

        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        assertTrue(violations.isEmpty(), "Validation should pass for ADMIN with INTERNAL UserType");
    }
    

    @Test
    void whenAdminWithExternalUserType_thenValidationFails() {
    	Admin admin = new Admin();
    	admin.setUserType(UserType.EXTERNAL);

        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);

        assertFalse(violations.isEmpty(), "Validation should fail for ADMIN with EXTERNAL UserType");
    }
    
}
