package com.fastcash.moneytransfer.validation;

import com.fastcash.moneytransfer.annotation.ValidAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidAccountValidator implements ConstraintValidator<ValidAccount, Long> {
	
	private final UserAccountRepository userAccountRepository;

    public ValidAccountValidator(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

	@Override
    public void initialize(ValidAccount constraintAnnotation) {
		
	}

    @Override
    public boolean isValid(Long accountId, ConstraintValidatorContext context) {
    	 // If accounts are equal, add a dynamic error message

    	if (accountId == null) {
            return true; // Let other validators handle null values
        }
    	
        return userAccountRepository.findById(accountId).isPresent();
    }
}