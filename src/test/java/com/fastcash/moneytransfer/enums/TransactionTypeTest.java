package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TransactionTypeTest {

	@Test
    void testTransactionTypeSizeNotEqualToZero() {
        assertNotEquals(0, TransactionType.values().length);
    }
	
	@Test
    void testGetDescription_OwnAccount() {
        // Arrange
        String description = "Own Account Transfer";
        
        // Act
        String actualDescription = TransactionType.OWN_ACCOUNT.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
        assertTrue(TransactionType.OWN_ACCOUNT.isInternal());
    }
	
	@Test
    void testGetDescription_AccounttoAccount() {
        // Arrange
        String description = "Account to Account Transfer";
        
        // Act
        String actualDescription = TransactionType.ACCOUNT_TO_ACCOUNT.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
        assertTrue(TransactionType.ACCOUNT_TO_ACCOUNT.isInternal());
    }

	@Test
    void testGetDescription_InterBank() {
        // Arrange
        String description = "Inter Bank Transfer";
        
        // Act
        String actualDescription = TransactionType.INTER_BANK.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
        assertFalse(TransactionType.INTER_BANK.isInternal());
    }
	
	@Test
    void testGetDescription_International() {
        // Arrange
        String description = "International Transfer";
        
        // Act
        String actualDescription = TransactionType.INTERNATIONAL.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
        assertFalse(TransactionType.INTERNATIONAL.isInternal());
    }

}
