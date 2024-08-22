package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AccountCategory;
import com.fastcash.moneytransfer.enums.Currency;

class TransactionAccountTest {
	
	@Test
    void testDefaultInitialization() {
        // Act
        TransactionAccount account = new TransactionAccount();

        // Assert
        assertNull(account.getId()); // Id should be null
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), account.getBalance()); // Initial balance should be zero
        assertTrue(account.isAllowOverdraft()); // Allow overdraft should be true
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    public void testGetAccountNumber_WhenAccountNumberIsNull_ShouldReturnId() {
		TransactionAccount account = new TransactionAccount();
		
		account.setId(12345L);
        account.setAccountNumber(null);
        assertEquals(12345L, account.getAccountNumber());
    }

    @Test
    public void testGetAccountNumber_WhenAccountNumberIsNotNull_ShouldReturnAccountNumber() {
    	TransactionAccount account = new TransactionAccount();
    	
    	account.setId(12345L);
    	account.setAccountNumber(67890L);
        assertEquals(67890L, account.getAccountNumber());
    }

	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        Long accountNumber = 0L;
        BigDecimal balance = BigDecimal.valueOf(1000.50);
        Currency currency = Currency.USD;
        boolean allowOverdraft = false;
        AccountCategory accountCategory = AccountCategory.INTERNAL_ACCOUNT;
        LocalDateTime createdAt = LocalDateTime.now();
        TransactionAccount account = new TransactionAccount();
        int version = 1;

        // Act
        account.setId(id);
        account.setCurrency(currency);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setAllowOverdraft(allowOverdraft);
        account.setAccountCategory(accountCategory);
        account.setCreatedAt(createdAt);
        account.setVersion(version);

        // Assert
        assertEquals(id, account.getId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(balance.setScale(Constants.BALANCE_SCALE), account.getBalance());
        assertEquals(currency, account.getCurrency());
        assertEquals(allowOverdraft, account.isAllowOverdraft());
        assertEquals(accountCategory, account.getAccountCategory());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(version, account.getVersion());
    }
	
}
