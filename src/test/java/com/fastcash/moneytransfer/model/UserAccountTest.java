package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AccountCategory;
import com.fastcash.moneytransfer.enums.Currency;

class UserAccountTest {
	
	private User user;
	private Currency currency;
	
	@BeforeEach
	void setUp() {
		user = new User();
		
		currency = Currency.NGN;
	}
	
	@Test
    void testDefaultInitialization() {
        // Act
        UserAccount userAccount = new UserAccount();

        // Assert
        assertNull(userAccount.getId()); // Id should be null
        assertNull(userAccount.getUser()); // User should be null
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), userAccount.getBalance()); // Initial balance should be zero
        assertTrue(userAccount.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(AccountCategory.USER_ACCOUNT, userAccount.getAccountCategory()); // UserAccount category should match
        assertEquals(0, userAccount.getDebitTransfers().size()); // Debit Transfers size should be zero
        assertNotNull(userAccount.getCreatedAt()); // CreatedAt should not be null
        assertFalse(userAccount.isDeleted()); // deleted should be false initially
    }
	
	@Test
    void testCurrencyAndUserInitialization() {
        // Act
        UserAccount userAccount = new UserAccount(currency, user);

        // Assert
        assertNull(userAccount.getId()); // Id should be null
        assertEquals(user, userAccount.getUser()); // User should be null
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), userAccount.getBalance()); // Initial balance should be zero
        assertEquals(currency, userAccount.getCurrency()); // Currency should match
        assertTrue(userAccount.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(AccountCategory.USER_ACCOUNT, userAccount.getAccountCategory()); // UserAccount category should match
        assertEquals(0, userAccount.getDebitTransfers().size()); // Debit Transfers size should be zero
        assertEquals(0, userAccount.getCreditTransfers().size()); // Credit Transfers size should be zero
        assertNotNull(userAccount.getCreatedAt()); // CreatedAt should not be null
        assertFalse(userAccount.isDeleted()); // deleted should be false initially
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        Long accountNumber = 0L;
        BigDecimal balance = BigDecimal.valueOf(1000.50);
        boolean allowOverdraft = false;
        LocalDateTime createdAt = LocalDateTime.now();
        UserAccount userAccount = new UserAccount();
        int version = 1;

        // Act
        userAccount.setId(id);
        userAccount.setCurrency(currency);
        userAccount.setAccountNumber(accountNumber);
        userAccount.setBalance(balance);
        userAccount.setAllowOverdraft(allowOverdraft);
        userAccount.setUser(user);
        userAccount.setDebitTransfers(List.of(new MoneyTransfer()));
        userAccount.setCreditTransfers(List.of(new MoneyTransfer()));
        userAccount.setCreatedAt(createdAt);
        userAccount.setDeleted(true);
        userAccount.setVersion(version);

        // Assert
        assertEquals(id, userAccount.getId());
        assertEquals(accountNumber, userAccount.getAccountNumber());
        assertEquals(balance.setScale(Constants.BALANCE_SCALE), userAccount.getBalance());
        assertEquals(currency, userAccount.getCurrency());
        assertEquals(allowOverdraft, userAccount.isAllowOverdraft());
        assertEquals(user, userAccount.getUser());
        assertEquals(AccountCategory.USER_ACCOUNT, userAccount.getAccountCategory());
        assertEquals(1, userAccount.getDebitTransfers().size()); 
        assertEquals(1, userAccount.getCreditTransfers().size());
        assertEquals(createdAt, userAccount.getCreatedAt());
        assertTrue(userAccount.isDeleted());
        assertEquals(version, userAccount.getVersion());
    }
	
}
