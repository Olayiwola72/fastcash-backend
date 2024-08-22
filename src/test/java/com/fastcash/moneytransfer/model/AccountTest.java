package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.Currency;

class AccountTest {
	
	@Test
    void testAccountInitialization() {
        // Act
        Account account = new Account();

        // Assert
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    void testCurrencyInitialization() {
        // Arrange
        Currency currency = Currency.NGN;

        // Act
        Account account = new Account(currency);

        // Assert
        assertEquals(currency, account.getCurrency()); // Currency should match
    }
	
	@Test
    void testCurrencyAccountNumberAndHolderNameInitialization() {
        // Arrange
        Currency currency = Currency.NGN;
        Long accountNumber = 0L;

        // Act
        Account account = new Account(currency, accountNumber);

        // Assert
        assertEquals(currency, account.getCurrency()); // Currency should match
        assertEquals(accountNumber, account.getAccountNumber()); 
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long accountNumber = 0L;
        Currency currency = Currency.USD;
        LocalDateTime createdAt = LocalDateTime.now();
        Account account = new Account();
        int version = 1;

        // Act
        account.setCurrency(currency);
        account.setAccountNumber(accountNumber);
        account.setCreatedAt(createdAt);
        account.setVersion(version);

        // Assert
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(currency, account.getCurrency());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(version, account.getVersion());
    }
	
}
