package com.fastcash.moneytransfer.validation;

import com.fastcash.moneytransfer.annotation.DebitCreditAccountNotEqual;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DebitCreditAccountNotEqualValidator implements ConstraintValidator<DebitCreditAccountNotEqual, MoneyTransferRequest> {
	
	@Override
    public void initialize(DebitCreditAccountNotEqual constraintAnnotation) {
		
	}
	
    @Override
    public boolean isValid(MoneyTransferRequest request, ConstraintValidatorContext context) {

    	if (request == null || request.debitAccount() == null || request.creditAccount() == null) {
            return true; // Let other validators handle null values
        }
        
        if (request.debitAccount().equals(request.creditAccount())) {
            // If accounts are equal, add a dynamic error message
            
            ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
            
        	if (violationBuilder != null) {
                violationBuilder.addPropertyNode("debitAccount") // Specify the field associated with the error
                	.addConstraintViolation(); // Add the violation
            }

            return false;
        }
        
        return true; // Validation passes
    }
}