package com.fastcash.moneytransfer.util;

import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilsTest {
	
	private UtilsTest() {
        // Private constructor to prevent instantiation
    }
	
	public static MockHttpServletResponse mockHttpGetRequest(MockMvc mockMvc, String apiUrl)
			throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(apiUrl);

		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}

	public static MockHttpServletResponse mockHttpGetRequestWithBearerToken(MockMvc mockMvc, String apiUrl, String token)
			throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(apiUrl);
		
		requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
	public static MockHttpServletResponse mockHttpGetRequestWithBasicAuth(MockMvc mockMvc, String apiUrl, String username, String password)
			throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(apiUrl);
		
		String credentials = username + ":" + password;
		String base64Credentials = Base64.getEncoder().encodeToString((credentials).getBytes());
		
		requestBuilder.header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);

		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
	
	public static MockHttpServletResponse mockHttpPostRequest(MockMvc mockMvc, String apiUrl)
			throws Exception {

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
	          .post(apiUrl);
	
		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
		
	public static MockHttpServletResponse mockHttpPostRequestWithBasicAuth(MockMvc mockMvc, String apiUrl, String username, String password)
			throws Exception {

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
	          .post(apiUrl);
	
		String credentials = username + ":" + password;
		String base64Credentials = Base64.getEncoder().encodeToString((credentials).getBytes());
		requestBuilder.header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
		requestBuilder.header(HttpHeaders.USER_AGENT, "JUNIT User Agent");
		
		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
	public static MockHttpServletResponse mockHttpPostRequestWithBasicAuth(MockMvc mockMvc, String apiUrl, String username, String password, String jsonContent)
			throws Exception {

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
	          .post(apiUrl);
	
		String credentials = username + ":" + password;
		String base64Credentials = Base64.getEncoder().encodeToString((credentials).getBytes());
		
		requestBuilder.header(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
		requestBuilder.content(jsonContent);
		requestBuilder.contentType(MediaType.APPLICATION_JSON);
		
		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
	
	public static String getToken(MockMvc mockMvc, String apiUrl, String username, String password)
			throws Exception {

		MockHttpServletResponse response = mockHttpPostRequestWithBasicAuth(mockMvc, apiUrl, username, password);
	
		// Parse the JSON response content for further assertions
		String jsonResponse = response.getContentAsString();
		JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);

		return rootNode.path("token").textValue();
	}
	
	public static MockHttpServletResponse mockHttpPostRequestWithBearerToken(MockMvc mockMvc, String apiUrl, String token, String jsonContent)
			throws Exception {
		
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(apiUrl);
		
		if(jsonContent != null) {
			requestBuilder.content(jsonContent);
			requestBuilder.contentType(MediaType.APPLICATION_JSON);
		}
	
		requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		
		// Performing the request and getting the MvcResult
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		// Extract the response from the MvcResult
		return mvcResult.getResponse();
	}
	
	public static String getNonExistentEndpointPath() {
		return "/api/nonexistent-endpoint";
	}
	
}
