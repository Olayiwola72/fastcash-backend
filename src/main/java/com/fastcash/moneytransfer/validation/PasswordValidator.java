package com.fastcash.moneytransfer.validation;

import org.springframework.beans.factory.annotation.Value;

import com.fastcash.moneytransfer.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
	
	private final String passwordPattern;

	public PasswordValidator(@Value("${password.regex}") String passwordPattern) {
		this.passwordPattern = passwordPattern;
	}
	
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    	
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
    	
        if (password == null || password.isEmpty()) { // let other validators handle null value
            return true;
        }
        
    	// Regular expression for password validation
        return password.matches(passwordPattern);
        
    }
}