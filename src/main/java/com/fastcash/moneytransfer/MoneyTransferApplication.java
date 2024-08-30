package com.fastcash.moneytransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.config.ExchangeRateConfig;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAsync
@EnableConfigurationProperties({
	ExchangeRateConfig.class,
	ApiProperties.class
})
public class MoneyTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyTransferApplication.class, args);
	}

}
