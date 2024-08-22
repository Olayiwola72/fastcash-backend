package com.fastcash.moneytransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private final String apiBaseUrl;
	
	public WebConfig(@Value("${api.base.url}") String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

	@Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiBaseUrl, c -> c.isAnnotationPresent(ApiBaseUrlPrefix.class));
    }
	
}
