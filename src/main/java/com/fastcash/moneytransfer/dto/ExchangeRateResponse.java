package com.fastcash.moneytransfer.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExchangeRateResponse(
		
	String result, 
	
	@JsonProperty("base_code")
	String baseCode, 
	
	@JsonProperty("target_code")
	String targetCode, 
	
	@JsonProperty("conversion_rate")
	BigDecimal conversionRate, 
	
	@JsonProperty("conversion_result")
	BigDecimal conversionResult, 
	
	@JsonProperty("error-type")
	String errorType
	
	) {
	
}