package com.fastcash.moneytransfer.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // adjust core pool size as needed
        executor.setMaxPoolSize(5); // adjust max pool size as needed
        executor.setQueueCapacity(50); // adjust queue capacity as needed
        executor.setThreadNamePrefix("AsyncEmailThread-");
        executor.initialize();
        return executor;
    }
    
}

