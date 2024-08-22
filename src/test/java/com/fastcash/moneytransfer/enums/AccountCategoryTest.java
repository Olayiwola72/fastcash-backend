package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class AccountCategoryTest {
	
	@Test
    void testAccountCategorySizeNotEqualToZero() {
        assertNotEquals(0, AccountCategory.values().length);
    }

	@Test
    void testGetChargeAmount_UserAccount() {
        // Arrange
        BigDecimal expectedChargeAmount = new BigDecimal("0.05");
        
        // Act
        BigDecimal actualChargeAmount = AccountCategory.USER_ACCOUNT.getChargeAmount();
        
        // Assert
        assertEquals(expectedChargeAmount, actualChargeAmount);
    }

    @Test
    void testGetChargeAmount_InternalAccount() {
        // Arrange
        BigDecimal expectedChargeAmount = BigDecimal.ZERO;
        
        // Act
        BigDecimal actualChargeAmount = AccountCategory.INTERNAL_ACCOUNT.getChargeAmount();
        
        // Assert
        assertEquals(expectedChargeAmount, actualChargeAmount);
    }

}
