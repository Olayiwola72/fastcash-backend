package com.fastcash.moneytransfer.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class CurrencyMismatchValidatorTest {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private TestEntityManager entityManager;

    @MockBean
    private ConstraintValidatorContext context;
    
    @MockBean
    private ConstraintViolationBuilder constraintViolationBuilder;
    
    @Mock
    private MessageSource messageSource;
    
    private CurrencyMismatchValidator validator;
    
    private User user;
    private UserAccount userAccount;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new CurrencyMismatchValidator(userAccountRepository, messageSource);
        
        user = new User("user@example.com", "password");
        entityManager.persist(user);
        entityManager.flush();
        
        userAccount = new UserAccount(Currency.USD, user);
        userAccount = userAccountRepository.save(userAccount);
    }

    @Test
    void testIsValid_WithValidRequest() {
        // Arrange
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
        // Set up the mock behavior for the MessageSource
        when(messageSource.getMessage("CurrencyMismatchError", null, LocaleContextHolder.getLocale())).thenReturn("CurrencyMismatchError");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));
        
    	MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK", userAccount.getId(), 2L, BigDecimal.TEN, Currency.NGN.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        
        // Verify that the buildConstraintViolation method was called once
        verify(context, times(1)).buildConstraintViolationWithTemplate(any());   
    }
    
    @Test
    void testIsValid_WithInvalidCreditCurrency_OwnAccount() {
        // Arrange
    	// Set up the mock behavior for the MessageSource
    	when(messageSource.getMessage("CurrencyMismatchError", null, LocaleContextHolder.getLocale())).thenReturn("CurrencyMismatchError");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));
        
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", 1L, userAccount.getId(), BigDecimal.TEN, Currency.NGN.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        
        // Verify that the buildConstraintViolation method was called once
        verify(context, times(1)).buildConstraintViolationWithTemplate(any());  
    }
    
}
