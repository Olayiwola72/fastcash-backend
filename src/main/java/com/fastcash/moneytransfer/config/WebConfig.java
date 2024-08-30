package com.fastcash.moneytransfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private String baseUrlPrefix;
	
	public WebConfig(
		ApiProperties apiProperties
	) {
		this.baseUrlPrefix = apiProperties.baseUrl() + apiProperties.version();
	}

	@Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(baseUrlPrefix, c -> c.isAnnotationPresent(ApiBaseUrlPrefix.class));
    }
	
}
