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

class InternalAccountTest {
	
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
        InternalAccount internalAccount = new InternalAccount();

        // Assert
        assertNull(internalAccount.getId()); // Id should be null
        assertEquals(AccountCategory.INTERNAL_ACCOUNT, internalAccount.getAccountCategory());
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), internalAccount.getBalance()); // Initial balance should be zero
        assertTrue(internalAccount.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(0, internalAccount.getExternalTransfers().size()); // External Transfers size should be zero
        assertNotNull(internalAccount.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    void testInternalAccountInitialization() {
        // Act
        InternalAccount internalAccount = new InternalAccount(currency, admin);

        // Assert
        assertNull(internalAccount.getId()); // Id should be null
        assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), internalAccount.getBalance()); // Initial balance should be zero
        assertEquals(currency, internalAccount.getCurrency()); // Currency should match
        assertTrue(internalAccount.isAllowOverdraft()); // Allow overdraft should be true
        assertEquals(AccountCategory.INTERNAL_ACCOUNT, internalAccount.getAccountCategory()); // Account category should match
        assertEquals(admin, internalAccount.getAdmin()); // Admin should match
        assertEquals(0, internalAccount.getExternalTransfers().size()); // External Transfers size should be zero
        assertNotNull(internalAccount.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        Long accountNumber = 0L;
        BigDecimal balance = BigDecimal.valueOf(1000.50);
        boolean allowOverdraft = false;
        LocalDateTime createdAt = LocalDateTime.now();
        InternalAccount account = new InternalAccount();
        int version = 1;

        // Act
        account.setId(id);
        account.setCurrency(currency);
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setAllowOverdraft(allowOverdraft);
        account.setAdmin(admin);;
        account.setExternalTransfers(List.of(new MoneyTransfer()));
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
        assertEquals(1, account.getExternalTransfers().size());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(version, account.getVersion());
    }
	
}
