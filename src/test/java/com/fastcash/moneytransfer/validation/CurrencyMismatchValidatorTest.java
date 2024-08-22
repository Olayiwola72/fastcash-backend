package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;

import jakarta.validation.ConstraintValidatorContext;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class CurrencyMismatchValidatorTest {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private TestEntityManager entityManager;

    @Mock
    private ConstraintValidatorContext context;
    
    private CurrencyMismatchValidator validator;
    
    private User user;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CurrencyMismatchValidator(userAccountRepository);
        
        user = new User("user@example.com", "password");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void testIsValid_WithValidRequest() {
        // Arrange
        UserAccount userAccount = new UserAccount(Currency.USD, user);
        userAccount = userAccountRepository.save(userAccount);
        
        MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK", userAccount.getId(), 2L, BigDecimal.TEN, Currency.USD.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertTrue(isValid);
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void testIsValid_WithInvalidDebitCurrency() {
        // Arrange
    	UserAccount userAccount = new UserAccount(Currency.NGN, user); // Different debit currency
    	userAccount = userAccountRepository.save(userAccount);
    	
    	MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK", userAccount.getId(), 2L, BigDecimal.TEN, Currency.USD.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        
        // Verify that the buildConstraintViolation method was called once
        verify(context, times(1)).buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());   
    }
    
    @Test
    void testIsValid_WithInvalidCreditCurrency_OwnAccount() {
        // Arrange
        UserAccount userAccount = new UserAccount(Currency.USD, user); // Different credit currency
        userAccount = userAccountRepository.save(userAccount);
        
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", 1L, userAccount.getId(), BigDecimal.TEN, Currency.NGN.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        
        // Verify that the buildConstraintViolation method was called once
        verify(context, times(1)).buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());   
    }
    
}
