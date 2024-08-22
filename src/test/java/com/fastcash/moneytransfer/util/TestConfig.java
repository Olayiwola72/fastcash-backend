package com.fastcash.moneytransfer.util;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@TestConfiguration
public class TestConfig {
	@Bean
	@Qualifier("handlerExceptionResolver")
	public HandlerExceptionResolver createDefaultHandlerExceptionResolver() {
	    return new DefaultHandlerExceptionResolver();
	}
	
	@Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
	
}
