package com.fastcash.moneytransfer.service;

import java.math.BigDecimal;

import com.fastcash.moneytransfer.dto.ExchangeRateResponse;
import com.fastcash.moneytransfer.exception.ExchangeRateException;

public interface ExchangeRateService {
	
	ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency) throws ExchangeRateException;

	ExchangeRateResponse getExchangeAmount(String baseCurrency, String targetCurrency, BigDecimal amount) throws ExchangeRateException;  
}