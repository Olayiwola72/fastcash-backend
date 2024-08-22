package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.Currency;

class ExternalAccountTest {
	
    private final Long accountNumber = 0L;
    private final String accountHolderName = "John Doe";
    private final String bankName = "Test Bank";
    
    
    @Test
    void testDefaultConstructor() {
        // Act
        ExternalAccount account = new ExternalAccount();

        // Assert
        assertNull(account.getId()); // Id should be null
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
    
    @Test
    public void testGetAccountNumber_WhenAccountNumberIsNotNull_ShouldReturnAccountNumber() {
    	ExternalAccount account = new ExternalAccount();
    	
    	account.setId(12345L);
    	account.setAccountNumber(67890L);
        assertEquals(67890L, account.getAccountNumber());
    }
	
	@Test
    void testExternalAccountInitialization() {
        // Act
		Currency currency = Currency.NGN;
        ExternalAccount account = new ExternalAccount(currency, accountNumber, accountHolderName, bankName);

        // Assert
        assertEquals(accountNumber, account.getId()); // Id should be null
        assertEquals(currency, account.getCurrency());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(accountHolderName, account.getAccountHolderName());
        assertEquals(bankName, account.getBankName());
        assertNotNull(account.getCreatedAt()); // CreatedAt should not be null
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        LocalDateTime createdAt = LocalDateTime.now();
        ExternalAccount account = new ExternalAccount();
        int version = 1;

        // Act
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setAccountHolderName(accountHolderName);
        account.setBankName(bankName);
        account.setCreatedAt(createdAt);
        account.setVersion(version);

        // Assert
        assertEquals(accountNumber, account.getId());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(accountHolderName, account.getAccountHolderName());
        assertEquals(bankName, account.getBankName());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(version, account.getVersion());
    }
	
}
