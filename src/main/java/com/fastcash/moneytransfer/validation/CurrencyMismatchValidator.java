package com.fastcash.moneytransfer.validation;

import java.util.Optional;

import com.fastcash.moneytransfer.annotation.CurrencyMismatch;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.util.ValidatorContextBuilder;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyMismatchValidator implements ConstraintValidator<CurrencyMismatch, MoneyTransferRequest> {

    private final UserAccountRepository userAccountRepository;

    public CurrencyMismatchValidator(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
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
                UserAccount debitAccount = debitAccountOptional.get();
                Currency debitCurrency = Currency.valueOf(request.debitCurrency());

                if (!debitAccount.getCurrency().equals(debitCurrency)) {
                    ValidatorContextBuilder.buildConstraintViolation(context, "debitCurrency");
                    return false;
                }
            }

            if (request.creditCurrency() != null && !request.creditCurrency().isBlank() && TransactionType.valueOf(request.transactionType()).equals(TransactionType.OWN_ACCOUNT)) {
                Optional<UserAccount> creditAccountOptional = userAccountRepository.findById(request.creditAccount());
                if (creditAccountOptional.isPresent()) {
                    UserAccount creditAccount = creditAccountOptional.get();
                    Currency creditCurrency = Currency.valueOf(request.creditCurrency());

                    if (!creditAccount.getCurrency().equals(creditCurrency)) {
                        ValidatorContextBuilder.buildConstraintViolation(context, "creditCurrency");
                        return false;
                    }
                }
            }

        } catch (NullPointerException | IllegalArgumentException e) {
        	return true;
        }

        return true; // Validation passes
    }
}
