package com.fastcash.moneytransfer.util;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;

import static org.junit.jupiter.api.Assertions.*;

class DataMaskerTest {
	
	private static final String accountMask = Constants.ACCOUNT_MASK;

    @Test
    void testMaskAccountId_ValidAccountId() {
        String accountId = "1234567890123456"; // example account number
        String expectedMaskedId = accountMask+"34567890123456"; // expected result
        String actualMaskedId = DataMasker.maskAccountId(accountId);

        assertEquals(expectedMaskedId, actualMaskedId, "The account ID should be correctly masked.");
    }

    @Test
    void testMaskAccountId_ShortAccountId() {
        String accountId = "123"; // example short account number
        String expectedMaskedId = accountMask+"3"; // should return "**3"
        String actualMaskedId = DataMasker.maskAccountId(accountId);

        assertEquals(expectedMaskedId, actualMaskedId, "The account ID should be correctly masked even if it is short.");
    }

    @Test
    void testMaskAccountId_ExactLengthAccountId() {
        String accountId = "**"; // account ID of the same length as the mask
        String expectedMaskedId = accountMask; // should return "**"
        String actualMaskedId = DataMasker.maskAccountId(accountId);

        assertEquals(expectedMaskedId, actualMaskedId, "The account ID should return the mask itself if it's of equal length.");
    }

    @Test
    void testMaskAccountId_NullAccountId() {
        String accountId = null; // null account number
        String actualMaskedId = DataMasker.maskAccountId(accountId);

        assertNull(actualMaskedId, "The account ID should be null if the input is null.");
    }
}
