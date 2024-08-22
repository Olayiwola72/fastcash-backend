package com.fastcash.moneytransfer.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {
	
	@Value("${allowed.origins}") 
	private List<String> allowedOrigins;
	
	@Autowired
    private MockMvc mockMvc;

    @Test
    void testCorsConfiguration() throws Exception {
    	for(String allowedOrigin : allowedOrigins) {
    		mockMvc.perform(options("/api/token")
	            .header("Origin", allowedOrigin)
	            .header("Access-Control-Request-Method", "POST")
	            .header("Access-Control-Request-Headers", "Authorization")
	            .header("Access-Control-Request-Headers", "Content-Type")
	        )
    		.andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", allowedOrigin))
            .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE,OPTIONS"))
            .andExpect(header().string("Access-Control-Allow-Headers", "Authorization, Content-Type"))
    		.andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    	}
    	
    }
}
