package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.service.impl.UUIDTimestampTransactionIdGenerator;

class UUIDTimestampTransactionIdGeneratorTest {
	
	@Test
	public void testGenerateTransactionId() {
        // Arrange
		TransactionIdGenerator transactionIdGenerator = new UUIDTimestampTransactionIdGenerator();

		// Act and Assert
        assertDoesNotThrow(() -> {
            String transactionId = transactionIdGenerator.generateTransactionId();

            // Additional assertions
            // Validate format: 6 alphanumeric characters followed by 13 digits
            assertTrue(transactionId.matches("[A-Z0-9]{6}\\d{13}"), "Transaction ID format is invalid: " + transactionId);
        });
    }
	
}
