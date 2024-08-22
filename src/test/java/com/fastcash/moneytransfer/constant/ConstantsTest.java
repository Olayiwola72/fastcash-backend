package com.fastcash.moneytransfer.constant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConstantsTest {
	
	@Test
    public void testJwtIssuer() {
        assertEquals("http://self", Constants.JWT_ISSUER);
    }

	@Test
    public void testAmountScale() {
        assertEquals(3, Constants.AMOUNT_SCALE);
    }
    
    @Test
    public void testBalanceScale() {
        assertEquals(3, Constants.BALANCE_SCALE);
    }
    
    @Test
    public void testAccountMask() {
        assertEquals("**", Constants.ACCOUNT_MASK);
    }
    
    @Test
    public void testPasswordResetTokenExpiryHours() {
        assertEquals(24, Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS);
    }

}
