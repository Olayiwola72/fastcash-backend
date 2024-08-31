package com.fastcash.moneytransfer.validation;

import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastcash.moneytransfer.annotation.CurrencyMismatch;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyMismatchValidator implements ConstraintValidator<CurrencyMismatch, MoneyTransferRequest> {

	private final UserAccountRepository userAccountRepository;
    private final MessageSource messageSource;

    public CurrencyMismatchValidator(UserAccountRepository userAccountRepository, MessageSource messageSource) {
        this.userAccountRepository = userAccountRepository;
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(CurrencyMismatch constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(MoneyTransferRequest request, ConstraintValidatorContext context) {
        if (request == null || request.debitAccount() == null || request.debitCurrency() == null || request.debitCurrency().isBlank() || request.creditAccount() == null) {
        	return true; // Let other validators handle null values
        }

        try {
        	
            Optional<UserAccount> debitAccountOptional = userAccountRepository.findById(request.debitAccount());
            
            if (debitAccountOptional.isPresent()) {
                Currency debitCurrency = Currency.valueOf(request.debitCurrency());

                if (!(debitAccountOptional.get().getCurrency().equals(debitCurrency))) {
                	buildConstraintViolation(context, debitCurrency, "debit", "debitCurrency");
                    return false;
                }
            }
            
            if (request.creditCurrency() != null && !request.creditCurrency().isBlank() && TransactionType.valueOf(request.transactionType()).isInternal()) {
                Optional<UserAccount> creditAccountOptional = userAccountRepository.findById(request.creditAccount());
                
                if (creditAccountOptional.isPresent()) {
                    Currency creditCurrency = Currency.valueOf(request.creditCurrency());
                    
                    if (!creditAccountOptional.get().getCurrency().equals(creditCurrency)) {
                        buildConstraintViolation(context, creditCurrency, "credit", "creditCurrency");
                        return false;
                    }
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {
        	return true;
        }
        
        return true; // Validation passes
    }
    
    private void buildConstraintViolation(ConstraintValidatorContext context, Currency currency, String type, String propertyNode) {
        
    	context.disableDefaultConstraintViolation();

        // Get the template and format it with dynamic values
        String messageTemplate = messageSource.getMessage("CurrencyMismatchError", null, LocaleContextHolder.getLocale());
        String formattedMessage = MessageFormat.format(messageTemplate, currency, type);
        
        if(context != null) {
        	context.buildConstraintViolationWithTemplate(formattedMessage)
            .addPropertyNode(propertyNode)
            .addConstraintViolation();
        }
    }
    
}
