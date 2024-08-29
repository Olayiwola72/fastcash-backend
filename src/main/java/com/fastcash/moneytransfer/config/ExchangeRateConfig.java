package com.fastcash.moneytransfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchange.rate.api")
public record ExchangeRateConfig(String url, String key, String plan) {
	
}
