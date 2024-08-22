package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ExchangeRateResponseTest {
	
	private final String result = "success";
	private final String baseCode = "NGN";
	private final String targetCode = "USD";
	private final BigDecimal conversionRate = BigDecimal.ONE;
	private final BigDecimal conversionResult = BigDecimal.TEN;
	private final String errorType = "unknown error";
		  
		
	@Test
	void testDefaultConstructor() {
		ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse(result, baseCode, targetCode, conversionRate, conversionResult, errorType);
		
		assertEquals(result, exchangeRateResponse.result());
		assertEquals(baseCode, exchangeRateResponse.baseCode());
		assertEquals(targetCode, exchangeRateResponse.targetCode());
		assertEquals(conversionRate, exchangeRateResponse.conversionRate());
		assertEquals(conversionResult, exchangeRateResponse.conversionResult());
		assertEquals(errorType, exchangeRateResponse.errorType());
	}
	
}

	  
