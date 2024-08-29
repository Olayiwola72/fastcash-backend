package com.fastcash.moneytransfer.service.impl;

import java.math.BigDecimal;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fastcash.moneytransfer.config.ExchangeRateConfig;
import com.fastcash.moneytransfer.dto.ExchangeRateResponse;
import com.fastcash.moneytransfer.enums.ExchangeRateApiResponseType;
import com.fastcash.moneytransfer.exception.ExchangeRateException;
import com.fastcash.moneytransfer.service.ExchangeRateService;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
	
	private final RestTemplate restTemplate;
    private final ExchangeRateConfig exchangeRateConfig;
    private final ReloadableResourceBundleMessageSource messageSource;
    
    public ExchangeRateServiceImpl(RestTemplate restTemplate, ExchangeRateConfig exchangeRateConfig, ReloadableResourceBundleMessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.exchangeRateConfig = exchangeRateConfig;
        this.messageSource = messageSource;
    }
	
	@Override
	public ExchangeRateResponse getExchangeRate(String baseCurrency, String targetCurrency) {
		String url = UriComponentsBuilder.fromHttpUrl(exchangeRateConfig.url())
                .pathSegment(exchangeRateConfig.key(), exchangeRateConfig.plan(), baseCurrency, targetCurrency)
                .toUriString();
		
		return fetchExchangeRateData(url);

    }
	
	@Override
    public ExchangeRateResponse getExchangeAmount(String baseCurrency, String targetCurrency, BigDecimal amount) throws ExchangeRateException {
        String url = UriComponentsBuilder.fromHttpUrl(exchangeRateConfig.url())
                .pathSegment(exchangeRateConfig.key(), exchangeRateConfig.plan(), baseCurrency, targetCurrency, String.valueOf(amount))
                .toUriString();
        
        return fetchExchangeRateData(url);
    }
	
	private ExchangeRateResponse fetchExchangeRateData(String url) throws ExchangeRateException {
        try {
			ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

            if (response != null) {
            	if(ExchangeRateApiResponseType.success.toString().equalsIgnoreCase(response.result())) {
            		return response;
            	}
            	
            	throw new ExchangeRateException (
                	messageSource.getMessage("ExchangeRetrievalFailure", null, LocaleContextHolder.getLocale())	+" "+ response.errorType()
                );
                
            } else {
            	throw new ExchangeRateException (
                	messageSource.getMessage("ExchangeRetrievalFailure", null, LocaleContextHolder.getLocale())
                );
            }
        } catch (Exception e) {
            throw new ExchangeRateException(
            	messageSource.getMessage("ExchangeRetrievalFailureError", null, LocaleContextHolder.getLocale()), 
            	e
            );
        }
    }
	
}
