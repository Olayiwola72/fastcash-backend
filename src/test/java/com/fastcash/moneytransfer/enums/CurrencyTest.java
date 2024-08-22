package com.fastcash.moneytransfer.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.dto.CurrencyResponse;

class CurrencyTest {
	
	@Test
    void testCurrencySizeNotEqualToZero() {
        assertNotEquals(0, Currency.values().length);
    }

	@Test
    void testGetRate_NGN() {
        // Arrange
        double expectedRate = 0.001;
        
        // Act
        double actualRate = Currency.NGN.getRate();
        
        // Assert
        assertEquals(expectedRate, actualRate);
    }

    @Test
    void testGetRate_USD() {
        // Arrange
    	double expectedRate = 1000;
        
    	// Act
        double actualRate = Currency.USD.getRate();
        
        // Assert
        assertEquals(expectedRate, actualRate);
    }
    
    @Test
    public void testGetCurrencyResponse() {
        List<CurrencyResponse> responses = Currency.getCurrencyResponse();

        // Check the size of the list
        assertEquals(Currency.values().length, responses.size(), "There should be 3 currencies.");
    }
    
}
