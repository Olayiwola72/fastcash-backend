package com.fastcash.moneytransfer.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {
	
	private final Locale defaultLocale;
	
	public LocaleConfig(
		@Value("${spring.mvc.locale}") Locale defaultLocale
	) {
		this.defaultLocale = defaultLocale;
	}

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(defaultLocale); // Set the default locale
        return localeResolver;
    }
}
