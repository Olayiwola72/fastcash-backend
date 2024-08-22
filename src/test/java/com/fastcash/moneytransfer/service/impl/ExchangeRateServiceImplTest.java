package com.fastcash.moneytransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

import com.fastcash.moneytransfer.config.ExchangeRateConfig;
import com.fastcash.moneytransfer.dto.ExchangeRateResponse;
import com.fastcash.moneytransfer.enums.ExchangeRateApiResponseType;
import com.fastcash.moneytransfer.exception.ExchangeRateException;

class ExchangeRateServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangeRateConfig exchangeRateConfig;

    @Mock
    private ReloadableResourceBundleMessageSource messageSource;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private ExchangeRateResponse successResponse;
    private ExchangeRateResponse failureResponse;
    private final String apiUrl = "https://api.exchangerate-api.com";
    private final String apiKey= "test-key";
    private final String successMessage = ExchangeRateApiResponseType.success.toString();
    private final String failureMessage = "Failed to retrieve exchange rate";
    private final String baseCurrency = "NGN";
    private final String targetCurrency = "USD";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize common test variables
        successResponse = new ExchangeRateResponse(successMessage, baseCurrency, targetCurrency, BigDecimal.valueOf(0.8412), BigDecimal.ONE, null);
        failureResponse = new ExchangeRateResponse("error", baseCurrency, targetCurrency, null, null, "invalid-key");
        
        // Mock common configurations
        when(exchangeRateConfig.getApiUrl()).thenReturn(apiUrl);
        when(exchangeRateConfig.getApiKey()).thenReturn(apiKey);
    }

    @Test
    void testGetExchangeRateSuccess() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(successResponse);

        // When
        ExchangeRateResponse response = exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);

        // Then
        assertNotNull(response);
        assertEquals(successResponse.result(), response.result());
        assertEquals(successResponse.conversionRate(), response.conversionRate());
    }

    @Test
    void testGetExchangeAmountSuccess() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(successResponse);

        // When
        ExchangeRateResponse response = exchangeRateService.getExchangeAmount(baseCurrency, targetCurrency, BigDecimal.TEN);

        // Then
        assertNotNull(response);
        assertEquals(successResponse.result(), response.result());
        assertEquals(successResponse.conversionResult(), response.conversionResult());
    }

    @Test
    void testGetExchangeRateFailure() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(failureResponse);
        when(messageSource.getMessage("ExchangeRetrievalFailureError", null, LocaleContextHolder.getLocale()))
                .thenReturn(failureMessage);

        // When / Then
        ExchangeRateException exception = assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.getExchangeRate(baseCurrency, targetCurrency);
        });

        assertNotNull(exception.getMessage());
        assertEquals(failureMessage, exception.getMessage());
    }

    @Test
    void testGetExchangeAmountFailure() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class))).thenReturn(failureResponse);
        when(messageSource.getMessage("ExchangeRetrievalFailureError", null, LocaleContextHolder.getLocale()))
                .thenReturn("Failed to retrieve exchange result");

        // When / Then
        ExchangeRateException exception = assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.getExchangeAmount(baseCurrency, targetCurrency, BigDecimal.TEN);
        });

        assertNotNull(exception.getMessage());
        assertEquals("Failed to retrieve exchange result", exception.getMessage());
    }
}
