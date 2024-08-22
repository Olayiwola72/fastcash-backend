package com.fastcash.moneytransfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.UtilsTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	private UserService userService;
	
	@MockBean
    private EmailNotificationService emailNotificationService;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	private User user;
	
	private final String tokenEndpoint;
	private final String loginEndpoint;
	
    public AuthControllerTest(
    	@Value("${api.base.url}") String apiBaseUrl, 
    	@Value("${endpoint.auth}") String authEndpoint,
    	@Value("${endpoint.token}") String tokenEndpoint,
    	@Value("${endpoint.login}") String loginEndpoint
    ) {
    	this.tokenEndpoint = apiBaseUrl + authEndpoint + tokenEndpoint;
        this.loginEndpoint = apiBaseUrl + authEndpoint + loginEndpoint;
    }
    
    @BeforeEach
	void setUp() {
    	String email = "login@test.com";
        user = new User(email, "password");
        
        userService.create(user);
	}
    
    @AfterEach
    void tearDown() {
    	refreshTokenRepository.deleteAll();
    	userService.deleteById(user.getId());
    }
    
    @Test
    public void testControllerIsAnnotatedWithApiBaseUrlPrefix() {
        boolean isAnnotated = AuthController.class.isAnnotationPresent(ApiBaseUrlPrefix.class);
        assertTrue(isAnnotated);
    }
	
	@Test
	void unauthenticatedRequestShouldReturnUnauthorized401() throws Exception {
		MockHttpServletResponse response = UtilsTest.mockHttpPostRequestWithBasicAuth(mockMvc, tokenEndpoint, "username", "password");

		// Validate against the MockHttpServletResponse
		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = {})
	void unauthorizedUserShouldReturnForbidden() throws Exception {
		MockHttpServletResponse response = UtilsTest.mockHttpPostRequest(mockMvc, tokenEndpoint);
		
		// Validate against the MockHttpServletResponse
		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
	}
	
	@Test
	void testToken() throws Exception {
		MockHttpServletResponse response = UtilsTest.mockHttpPostRequestWithBasicAuth(mockMvc, tokenEndpoint, user.getEmail(), "password");
				
		// Validate against the MockHttpServletResponse
		assertEquals(HttpStatus.OK.value(), response.getStatus());

		// Parse the JSON response content for further assertions
		String jsonResponse = response.getContentAsString();
		JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);
		
		assertNotNull(rootNode.path("token").textValue());	
		assertNotNull(rootNode.path("refreshToken").textValue());	
        assertNotNull(userService.findById(user.getId()).get().getLastLoginDate());
	}
	
	@Test
	void testLogin() throws Exception {
		MockHttpServletResponse response = UtilsTest.mockHttpPostRequestWithBasicAuth(mockMvc, loginEndpoint, user.getEmail(), "password");
				
		// Validate against the MockHttpServletResponse
		assertEquals(HttpStatus.OK.value(), response.getStatus());

		// Parse the JSON response content for further assertions
		String jsonResponse = response.getContentAsString();
		JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);
		
		assertNotNull(rootNode.path("token").textValue());
		assertNotNull(rootNode.path("refreshToken").textValue());	
		assertNotNull(rootNode.path("successMessage").textValue());
		
		// Check userData
		rootNode = rootNode.path("userData");
        
        assertEquals(user.getEmail(), rootNode.path("email").textValue());
		assertEquals(user.getRoles(), rootNode.path("roles").textValue());
		assertTrue(rootNode.path("enabled").asBoolean());
		
		assertTrue(rootNode.path("transfers").isArray());
        assertEquals(0, rootNode.path("transfers").size());
        
        assertTrue(rootNode.path("accounts").isArray());
        assertEquals(Currency.values().length, rootNode.path("accounts").size());

        assertEquals(user.getId(), rootNode.path("id").asLong());
        assertEquals(user.getAuthMethod(), AuthMethod.valueOf(rootNode.path("authMethod").textValue()));
        
        assertNotNull(rootNode.path("createdAt").textValue());
    	assertNull(rootNode.path("deletedAt").textValue());
    	
    	assertNull(rootNode.path("chargeAccounts").textValue());
    	assertNotNull(rootNode.path("lastLoginDate").textValue());
	}
	
}
