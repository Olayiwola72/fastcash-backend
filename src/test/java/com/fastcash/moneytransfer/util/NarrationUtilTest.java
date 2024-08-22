package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.TransactionType;

public class NarrationUtilTest {
	
	private final String notes = "notes";
	
	@Test
    void testGetNarration_WithNotes() {
        assertEquals(
    		TransactionType.OWN_ACCOUNT.getDescription() + ", "+ notes,
    		NarrationUtil.getNarration(TransactionType.OWN_ACCOUNT, notes)
        );
    }
	
	@Test
    void testGetNarration_WithoutNullNotes() {
        assertEquals(
    		TransactionType.OWN_ACCOUNT.getDescription(),
    		NarrationUtil.getNarration(TransactionType.OWN_ACCOUNT, null)
        );
    }
	
	@Test
    void testGetNarration_WithEmptyNotes() {
        assertEquals(
    		TransactionType.OWN_ACCOUNT.getDescription(),
    		NarrationUtil.getNarration(TransactionType.OWN_ACCOUNT, "")
        );
    }
}
