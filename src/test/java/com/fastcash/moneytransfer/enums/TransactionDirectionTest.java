package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TransactionDirectionTest {

	@Test
    void testTransactionDirectionSizeNotEqualToZero() {
        assertNotEquals(0, TransactionDirection.values().length);
    }
	
	@Test
    public void testDebitDescription() {
        assertEquals("Sending", TransactionDirection.DEBIT.getDescription());
    }

    @Test
    public void testDebitSign() {
        assertEquals('-', TransactionDirection.DEBIT.getSign());
    }

    @Test
    public void testCreditDescription() {
        assertEquals("Receiving", TransactionDirection.CREDIT.getDescription());
    }

    @Test
    public void testCreditSign() {
        assertEquals('+', TransactionDirection.CREDIT.getSign());
    }

}
