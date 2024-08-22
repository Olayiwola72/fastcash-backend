package com.fastcash.moneytransfer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.model.UserAccount;

class InsufficientBalanceExceptionTest {

	@Test
    void testConstructorAndGetters() {
        // Arrange
        String message = "Insufficient";
        String code = "InsufficientBalance";
        UserAccount userAccount = new UserAccount();
        BigDecimal amount = new BigDecimal(0);
        String fieldName = "disabledField";
        
        // Act
        InsufficientBalanceException exception = new InsufficientBalanceException(message, code, userAccount, new BigDecimal(0), fieldName);
        
        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
        assertEquals(userAccount, exception.getAccount());
        assertEquals(amount, exception.getAmount());
        assertEquals(fieldName, exception.getFieldName());
    }

}
