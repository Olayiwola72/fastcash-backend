package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForCharSequence;
import org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.DecimalMinValidatorForBigDecimal;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForCharSequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.validation.CurrencyMismatchValidator;
import com.fastcash.moneytransfer.validation.DebitCreditAccountNotEqualValidator;
import com.fastcash.moneytransfer.validation.ValidAccountValidator;
import com.fastcash.moneytransfer.validation.ValidEnumValidator;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@MockitoSettings(strictness = Strictness.LENIENT)
class MoneyTransferRequestTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private MessageSource messageSource;

    private Validator validator;

    private User user;

    private UserAccount debitAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ConstraintValidatorFactory constraintValidatorFactory = mock(ConstraintValidatorFactory.class);

        when(constraintValidatorFactory.getInstance(ValidAccountValidator.class))
            .thenReturn(new ValidAccountValidator(userAccountRepository));

        when(constraintValidatorFactory.getInstance(CurrencyMismatchValidator.class))
            .thenReturn(new CurrencyMismatchValidator(userAccountRepository, messageSource));

        when(constraintValidatorFactory.getInstance(NotEmptyValidatorForCharSequence.class))
            .thenReturn(new NotEmptyValidatorForCharSequence());

        when(constraintValidatorFactory.getInstance(SizeValidatorForCharSequence.class))
            .thenReturn(new SizeValidatorForCharSequence());

        when(constraintValidatorFactory.getInstance(NotNullValidator.class))
            .thenReturn(new NotNullValidator());

        when(constraintValidatorFactory.getInstance(NotBlankValidator.class))
            .thenReturn(new NotBlankValidator());

        when(constraintValidatorFactory.getInstance(ValidEnumValidator.class))
        .thenAnswer(invocation -> {
            ValidEnumValidator validator = new ValidEnumValidator();
            return validator;
        });
        
        when(constraintValidatorFactory.getInstance(DebitCreditAccountNotEqualValidator.class))
            .thenReturn(new DebitCreditAccountNotEqualValidator());

        when(constraintValidatorFactory.getInstance(DecimalMinValidatorForBigDecimal.class))
            .thenReturn(new DecimalMinValidatorForBigDecimal());

        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
            .configure()
            .constraintValidatorFactory(constraintValidatorFactory)
            .buildValidatorFactory();

        validator = validatorFactory.getValidator();

        user = new User("test@email.com", "password");
        debitAccount = new UserAccount(Currency.NGN, user);
        debitAccount.setId(10000L);
        when(userAccountRepository.findById(debitAccount.getId())).thenReturn(Optional.of(debitAccount));
    }

    @Test
    void testValidAccount_InvalidDebitAccount() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", 1L, 2L, BigDecimal.TEN, "NGN", "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size(), "Expected exactly one violation");

        boolean hasDebitAccountViolation = violations.stream()
            .anyMatch(violation -> "debitAccount".equals(violation.getPropertyPath().toString()));

        assertTrue(hasDebitAccountViolation, "Expected a violation for debitAccount but found: " + violations);
    }

    @Test
    void testValidRequest() {
        MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK", debitAccount.getId(), 1L, BigDecimal.TEN, debitAccount.getCurrency().toString(), "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_WithEmptyOrNullValues() {
        Set<String> expectedPropertyPaths = new HashSet<>(List.of("transactionType", "debitAccount", "creditAccount", "amount", "debitCurrency", "creditCurrency", "conversionRate", "conversionAmount"));
        Set<String> actualPropertyPaths = new HashSet<>();

        MoneyTransferRequest request = new MoneyTransferRequest(null, null, null, null, null, null, null, null, null, null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        for (ConstraintViolation<MoneyTransferRequest> violation : violations) {
            actualPropertyPaths.add(violation.getPropertyPath().toString());
        }

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 10, "Expected violations for each null field");
        assertEquals(expectedPropertyPaths, actualPropertyPaths);
    }

    @Test
    void testDebitCreditAccountNotEqual() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", 10002L, 10002L, BigDecimal.TEN, "NGN", "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testCurrencyMismatch() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", 10002L, 10003L, BigDecimal.TEN, "USD", "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_WithZeroAmount() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), 1L, BigDecimal.ZERO, debitAccount.getCurrency().toString(), "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size(), "Expected exactly one violation");

        boolean hasAmountViolation = violations.stream()
            .anyMatch(violation -> "amount".equals(violation.getPropertyPath().toString()));

        assertTrue(hasAmountViolation, "Expected a violation for amount but found: " + violations);
    }

    @Test
    void testInvalidRequest_WithNegativeAmount() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), 1L, BigDecimal.TEN.negate(), debitAccount.getCurrency().toString(), "USD", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size(), "Expected exactly one violation");

        boolean hasAmountViolation = violations.stream()
            .anyMatch(violation -> "amount".equals(violation.getPropertyPath().toString()));

        assertTrue(hasAmountViolation, "Expected a violation for amount but found: " + violations);
    }

    @Test
    void testInvalidRequest_WithInvalidCurrencies() {
        Set<String> expectedPropertyPaths = new HashSet<>(List.of("debitCurrency", "creditCurrency"));
        Set<String> actualPropertyPaths = new HashSet<>();

        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), 10007L, BigDecimal.TEN, "AAAX", "BBBB", BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        for (ConstraintViolation<MoneyTransferRequest> violation : violations) {
            actualPropertyPaths.add(violation.getPropertyPath().toString());
        }

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size(), "Expected exactly two violations");
        assertEquals(expectedPropertyPaths, actualPropertyPaths);
    }

    @Test
    void testInvalidRequest_WithInvalidStringLengthGT35() {
        String mockString = RandomStringUtils.randomAlphanumeric(36);
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), 1L, BigDecimal.TEN, debitAccount.getCurrency().toString(), "USD", BigDecimal.ONE, BigDecimal.TEN, mockString, mockString, mockString);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size(), "Expected three violations for fields exceeding maximum length");
    }

    @Test
    void testValidRequest_WithOptionalFields() {
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), 1L, BigDecimal.TEN, debitAccount.getCurrency().toString(), "USD", BigDecimal.ONE, BigDecimal.TEN, null, null, null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testJsonCreator() {
        // Create input values
        Long debitAccount = 10002L;
        Long creditAccount = 10003L;
        BigDecimal amount = BigDecimal.ONE;
        String debitCurrency = "NGN";
        String creditCurrency = "USD";
        String notes = "this is a test transfer";
        String accountHolderName = "John Doe";
        String bankName = "Test Bank";
        BigDecimal conversionRate = BigDecimal.valueOf(0.01);
        BigDecimal conversionAmount = BigDecimal.TEN;

        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("OWN_ACCOUNT", debitAccount, creditAccount, amount, debitCurrency, creditCurrency, conversionRate, conversionAmount, notes, accountHolderName, bankName);

        // Assert that the instance is created successfully
        assertNotNull(request);
        assertEquals(debitAccount, request.debitAccount());
        assertEquals(creditAccount, request.creditAccount());
        assertEquals(amount, request.amount());
        assertEquals(debitCurrency, request.debitCurrency());
        assertEquals(creditCurrency, request.creditCurrency());
        assertEquals(notes, request.notes());
        assertEquals(accountHolderName, request.accountHolderName());
        assertEquals(bankName, request.bankName());
    }
}
