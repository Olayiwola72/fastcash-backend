package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class InternalChargeAccountTest {
	
	private Admin admin;
	private Currency currency;
	
	@BeforeEach
	void setUp() {
		admin = new Admin();
		
		currency = Currency.NGN;
	}
	
	@Test
    void testDefaultInitialization() {
        // Act
		InternalChargeAccount account = new InternalChargeAccount();

        // Assert
        assertNull(account.getId()); // Id should be null
        assertEquals(AccountCategory.INTERNAL_ACCOUNT, account.getAccountCategory());
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), account.getBalance()); // Initial balance should be zero
        assertTrue(account.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(0, account.getChargedTransfers().size()); // Charged Transfers size should be zero
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    void testInternalAccountInitialization() {
        // Act
        InternalChargeAccount account = new InternalChargeAccount(currency, admin);

        // Assert
        assertNull(account.getId()); // Id should be null
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), account.getBalance()); // Initial balance should be zero
        assertEquals(currency, account.getCurrency()); // Currency should match
        assertTrue(account.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(AccountCategory.INTERNAL_ACCOUNT, account.getAccountCategory()); // Account category should match
        assertEquals(admin, account.getAdmin()); // User should match
        assertEquals(0, account.getChargedTransfers().size()); // Charged Transfers size should be zero
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        Long accountNumber = 0L;
        BigDecimal balance = BigDecimal.valueOf(1000.50);
        boolean allowOverdraft = false;
        LocalDateTime createdAt = LocalDateTime.now();
        InternalChargeAccount account = new InternalChargeAccount();
        int version = 1;

        // Act
        account.setId(id);
        account.setCurrency(currency);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setAllowOverdraft(allowOverdraft);
        account.setAdmin(admin);
        account.setChargedTransfers(List.of(new MoneyTransfer()));
        account.setCreatedAt(createdAt);
        account.setVersion(version);

        // Assert
        assertEquals(id, account.getId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(balance.setScale(Constants.BALANCE_SCALE), account.getBalance());
        assertEquals(currency, account.getCurrency());
        assertEquals(allowOverdraft, account.isAllowOverdraft());
        assertEquals(admin, account.getAdmin());
        assertEquals(AccountCategory.INTERNAL_ACCOUNT, account.getAccountCategory());
        assertEquals(1, account.getChargedTransfers().size());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(version, account.getVersion());
    }
	
}
