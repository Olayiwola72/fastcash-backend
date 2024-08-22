package com.fastcash.moneytransfer.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fastcash.moneytransfer.dto.CurrencyResponse;

public enum Currency {
	
	NGN(0.001), // 1 NGN = 0.001 USD
    USD(1000), // 1 USD = 1000 NGN
	GBP(1500);

    private final double rate;

    Currency(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }
    
    public static List<CurrencyResponse> getCurrencyResponse() {
        return Arrays.stream(Currency.values())
                .map(currency -> new CurrencyResponse(currency.name(), currency.getRate()))
                .collect(Collectors.toList());
    }
}