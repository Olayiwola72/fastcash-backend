package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    }

	@Test
    void testGetDescription_InterBank() {
        // Arrange
        String description = "Inter Bank Transfer";
        
        // Act
        String actualDescription = TransactionType.INTER_BANK.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
    }
	
	@Test
    void testGetDescription_International() {
        // Arrange
        String description = "International Transfer";
        
        // Act
        String actualDescription = TransactionType.INTERNATIONAL.getDescription();
        
        // Assert
        assertEquals(description, actualDescription);
    }

}
