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

import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.enums.Currency;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class DebitCreditAccountNotEqualValidatorTest {
	
    private DebitCreditAccountNotEqualValidator validator;
	
	@Mock
    private ConstraintValidatorContext context;
	
	@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DebitCreditAccountNotEqualValidator();
    }
	
	@Test
    void testIsValid_WithValidRequest() {
        // Arrange
        MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK",1L, 2L, BigDecimal.TEN, Currency.USD.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertTrue(isValid);
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }
	
    @Test
    void testIsValid_WithSameDebitAndCreditAccount() {
        // Arrange
    	MoneyTransferRequest request = new MoneyTransferRequest("INTER_BANK",1L, 1L, BigDecimal.TEN, Currency.USD.toString(), Currency.NGN.toString(), BigDecimal.ONE, BigDecimal.TEN, "", null, null);

        // Act
        boolean isValid = validator.isValid(request, context);

        // Assert
        assertFalse(isValid);
        
        // Verify that the buildConstraintViolation method was called once
        verify(context, times(1)).buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());        
    }
}
