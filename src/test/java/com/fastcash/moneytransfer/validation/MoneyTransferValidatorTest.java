package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class MoneyTransferValidatorTest {
	
	private static Validator validator;
	
	private User user;
	private Admin admin;

    @BeforeAll
    static void setUpAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @BeforeEach
    void setUp() {
        user = new User();
        admin = new Admin();
    }

    @Test
    void whenOwnAccountWithInvalidFields_thenValidationFails() {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        moneyTransfer.setChargeAmount(BigDecimal.ONE); // invalid field for own account
        moneyTransfer.setTotalDebitedAmount(BigDecimal.TEN);
        moneyTransfer.setTotalCreditedAmount(BigDecimal.TEN);
        moneyTransfer.setAmount(BigDecimal.TEN);
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setTransactionId("111");
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setDebitAccount(new UserAccount(moneyTransfer.getDebitCurrency(), user));
        moneyTransfer.setCreditAccount(new UserAccount(moneyTransfer.getCreditCurrency(), user));
        moneyTransfer.setInternalAccount(new InternalAccount(moneyTransfer.getCreditCurrency(), admin)); // invalid field for own account
        moneyTransfer.setInternalChargeAccount(new InternalChargeAccount(moneyTransfer.getCreditCurrency(), admin)); // invalid field for own account

        Set<ConstraintViolation<MoneyTransfer>> violations = validator.validate(moneyTransfer);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid OWN_ACCOUNT fields");
        assertEquals(1, violations.size());
    }

    @Test
    void whenInterBankWithValidFields_thenValidationSucceeds() {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionType(TransactionType.INTER_BANK);
        moneyTransfer.setChargeAmount(BigDecimal.ONE);
        moneyTransfer.setTotalDebitedAmount(BigDecimal.TEN);
        moneyTransfer.setTotalCreditedAmount(BigDecimal.TEN);
        moneyTransfer.setAmount(BigDecimal.TEN);
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setTransactionId("111");
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setDebitAccount(new UserAccount(moneyTransfer.getDebitCurrency(), user));
        moneyTransfer.setCreditAccount(new UserAccount(moneyTransfer.getCreditCurrency(), user));
        moneyTransfer.setInternalAccount(new InternalAccount(moneyTransfer.getCreditCurrency(), admin)); 
        moneyTransfer.setInternalChargeAccount(new InternalChargeAccount(moneyTransfer.getCreditCurrency(), admin));
        

        Set<ConstraintViolation<MoneyTransfer>> violations = validator.validate(moneyTransfer);
        assertTrue(violations.isEmpty(), "Validation should pass for valid INTER_BANK fields");
    }

    @Test
    void whenInternationalWithInvalidFields_thenValidationFails() {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionType(TransactionType.INTERNATIONAL);
        moneyTransfer.setInternalAccount(null); // required fields for INTERNATIONAL
        moneyTransfer.setInternalChargeAccount(null); // required fields for INTERNATIONAL
        moneyTransfer.setChargeAmount(BigDecimal.ZERO); // required fields for INTERNATIONAL
        moneyTransfer.setTotalDebitedAmount(BigDecimal.TEN);
        moneyTransfer.setTotalCreditedAmount(BigDecimal.TEN);
        moneyTransfer.setAmount(BigDecimal.TEN);
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setTransactionId("111");
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setDebitAccount(new UserAccount(moneyTransfer.getDebitCurrency(), user));
        moneyTransfer.setCreditAccount(new UserAccount(moneyTransfer.getCreditCurrency(), user));

        Set<ConstraintViolation<MoneyTransfer>> violations = validator.validate(moneyTransfer);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid INTERNATIONAL fields");
        assertEquals(1, violations.size());
    }

    @Test
    void whenInterCurrencyWithValidFields_thenValidationSucceeds() {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setCreditCurrency(Currency.USD);
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setTotalDebitedAmount(BigDecimal.TEN);
        moneyTransfer.setTotalCreditedAmount(BigDecimal.TEN);
        moneyTransfer.setAmount(BigDecimal.TEN);
        moneyTransfer.setTransactionId("111");
        moneyTransfer.setDebitAccount(new UserAccount(moneyTransfer.getDebitCurrency(), user));
        moneyTransfer.setCreditAccount(new UserAccount(moneyTransfer.getCreditCurrency(), user));

        Set<ConstraintViolation<MoneyTransfer>> violations = validator.validate(moneyTransfer);
        assertTrue(violations.isEmpty(), "Validation should pass for valid INTER_CURRENCY fields");
    }

    @Test
    void whenInterCurrencyWithInvalidFields_thenValidationFails() {
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setTotalDebitedAmount(BigDecimal.TEN);
        moneyTransfer.setTotalCreditedAmount(BigDecimal.TEN);
        moneyTransfer.setAmount(BigDecimal.TEN);
        moneyTransfer.setTransactionId("111");
        moneyTransfer.setConversionRate(BigDecimal.valueOf(0.01)); // rate must be equal to 1
        moneyTransfer.setDebitAccount(new UserAccount(moneyTransfer.getDebitCurrency(), user));
        moneyTransfer.setCreditAccount(new UserAccount(moneyTransfer.getCreditCurrency(), user));

        Set<ConstraintViolation<MoneyTransfer>> violations = validator.validate(moneyTransfer);
        assertFalse(violations.isEmpty(), "Validation should fail for same currency fields with invalid rate");
        assertEquals(1, violations.size());
        
    }
    
}
