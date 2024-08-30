package com.fastcash.moneytransfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.util.UtilsTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ConfigControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Value("${app.admin.email}") 
	private String adminEmail;
	
	@Value("${app.admin.password}") 
	private String adminPassword;
	
	@Autowired
    private ApiProperties apiProperties;
	
	@Test
    public void testControllerIsAnnotatedWithApiBaseUrlPrefix() {
        boolean isAnnotated = ConfigController.class.isAnnotationPresent(ApiBaseUrlPrefix.class);
        assertTrue(isAnnotated);
    }
	
	@Test
	void shouldReturnAllEnums() throws Exception {
		MockHttpServletResponse response = UtilsTest.mockHttpGetRequestWithBasicAuth(mockMvc, apiProperties.fullConfigPath(), adminEmail, adminPassword);
		
		// Validate against the MockHttpServletResponse
		assertEquals(HttpStatus.OK.value(), response.getStatus());

		// Parse the JSON response content for further assertions
		String jsonResponse = response.getContentAsString();
		JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);
		
		assertNotNull(rootNode.path("todayDate"));

		assertEquals(true, rootNode.path("currencies").isArray());
        assertEquals(Currency.values().length, rootNode.path("currencies").size());
        
        assertEquals(true, rootNode.path("providers").isArray());
        assertEquals(AuthMethod.values().length, rootNode.path("providers").size());
        
        assertEquals(true, rootNode.path("transactionTypes").isArray());
        assertEquals(TransactionType.values().length, rootNode.path("transactionTypes").size());
	}
}
