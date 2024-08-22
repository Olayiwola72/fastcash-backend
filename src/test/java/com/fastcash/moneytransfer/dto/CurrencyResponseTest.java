package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CurrencyResponseTest {
	
	private final String name = "USD";
	private final double buyRate = 0.1;
	
	@Test
	void testCurrencyResponseDefaultConstructor() {
		CurrencyResponse currencyResponse = new CurrencyResponse(name, buyRate);
		
		assertEquals(name, currencyResponse.name());
		assertEquals(buyRate, currencyResponse.buyRate());
	}
}
