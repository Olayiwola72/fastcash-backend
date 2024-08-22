package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ExchangeRateApiResponseTypeTest {

	@Test
    void testExchangeRateApiResponseType() {
        assertNotEquals(0, ExchangeRateApiResponseType.values().length);
    }
}
