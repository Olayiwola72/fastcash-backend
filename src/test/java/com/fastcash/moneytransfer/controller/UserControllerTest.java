package com.fastcash.moneytransfer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.GoogleUserResponse;
import com.fastcash.moneytransfer.dto.PasswordUpdateRequest;
import com.fastcash.moneytransfer.dto.UserRequest;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.dto.UserUpdateRequest;
import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.RefreshTokenRepository;
import com.fastcash.moneytransfer.repository.TransactionAccountRepository;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DefaultUserDetailsChecker;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.GoogleAuthService;
import com.fastcash.moneytransfer.service.RefreshTokenService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.ExistingUsernameValidator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(UserController.class)
@Import({
	MessageSourceConfig.class,
	TokenAuthenticationService.class,
	SecurityConfig.class,
	RsaKeyConfig.class,
	RSAKeyPairGenerator.class,
	KeyPairFileUtil.class,
	DelegatedAuthenticationEntryPoint.class, 
	DelegatedBearerTokenAccessDeniedHandler.class,
	PasswordConfig.class,
	UserService.class,
	UserValidator.class,
	AccountService.class,
	UserTypeChecker.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	RefreshTokenService.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private UserAccountRepository userAccountRepository;
    
    @MockBean
    private TransactionAccountRepository transactionAccountRepository;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private UserRequestMapper userRequestMapper;

    @MockBean
    private ExistingUsernameValidator existingUsernameValidator;

    @MockBean
    private GoogleAuthService googleAuthService;

    @MockBean
    private APIResponse controllerResponse;
    
    @MockBean
    private UserUpdateAuthorizationManager userUpdateAuthorizationManager;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @MockBean
    private RefreshTokenService refreshTokenService;
    
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    
	private final String userEndpoint;
    
    UserControllerTest(
    	@Value("${api.base.url}") String apiBaseUrl, 
    	@Value("${endpoint.user}") String userEndpoint
    ) {
        this.userEndpoint = apiBaseUrl + userEndpoint;
    }

    private User user;
    private UserRequest userRequest;
    private PasswordUpdateRequest passwordUpdateRequest;
    private UserUpdateRequest userUpdateRequest;
    private RefreshToken refreshToken;
    private final String userAgent = "userAgent";

    @BeforeEach
    void setup() {
        user = new User(); // Set necessary fields for the user
        user.setId(1L);
        user.setEmail("test@33.cc");
        userRequest = new UserRequest(user.getEmail(), "123456@Axc2"); // Set necessary fields for the userRequest
        passwordUpdateRequest = new PasswordUpdateRequest("123456@Axc2xc"); // Set necessary fields for the passwordUpdateRequest
        userUpdateRequest = new UserUpdateRequest("John Doe"); // Set necessary fields for the userUpdateRequest
        
        refreshToken = new RefreshToken("token", LocalDateTime.now().plusMinutes(50), user, userAgent);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(get(userEndpoint + "/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        		.andExpect(jsonPath("$.id").value(user.getId()));

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
    	doNothing().when(existingUsernameValidator).isEmailExisting(userRequest.email());
        when(userRequestMapper.toUser(any(UserRequest.class))).thenReturn(user);
        when(userService.create(any(User.class))).thenReturn(user);
        when(tokenAuthenticationService.authenticateUser(anyString(), anyString())).thenReturn("token");
        when(refreshTokenService.createRefreshToken(eq(user), eq(userAgent))).thenReturn(refreshToken);

        // Perform POST request with User-Agent header
        mockMvc.perform(post(userEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)) // Adjust JSON payload as needed
                .header("User-Agent", userAgent)) // Include User-Agent header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
                .andExpect(jsonPath("$.token").isNotEmpty()) // token is not null
                .andExpect(jsonPath("$.refreshToken").isNotEmpty()) // refreshToken is not null
                .andExpect(jsonPath("$.userData").isNotEmpty()); // userData is not null


        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUserWithGoogle() throws Exception {
        // Mocking dependencies
        GoogleUserResponse googleUserResponse = new GoogleUserResponse(null, user);
        when(googleAuthService.getUser(anyString())).thenReturn(googleUserResponse);
        when(tokenAuthenticationService.authenticateUser(anyString(), any(HttpServletRequest.class))).thenReturn(mock(Authentication.class));
        when(tokenAuthenticationService.generateToken(any(Authentication.class), eq(googleUserResponse.existingUser()))).thenReturn("token");
        when(refreshTokenService.findByUserAndUserAgent(eq(user), eq(userAgent))).thenReturn(refreshToken);
        
        String jsonContent = "{\n" + 
                             "    \"credential\": \"credential\",\n" + 
                             "    \"clientId\": \"clientId\",\n" +
                             "    \"select_by\": \"select_by\"\n" + 
                             "}";

        // Perform POST request with User-Agent header
        mockMvc.perform(post(userEndpoint + "/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .header("User-Agent", userAgent)) // Include User-Agent header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
                .andExpect(jsonPath("$.token").isNotEmpty()) // token is not null
                .andExpect(jsonPath("$.refreshToken").isNotEmpty()) // refreshToken is not null
                .andExpect(jsonPath("$.userData").isNotEmpty()); // userData is not null

        // Verify that the getUser method was called exactly once
        verify(googleAuthService, times(1)).getUser(anyString());
    }


    @Test
    @WithMockUser(roles = "USER")
    void testUpdateUser() throws Exception {
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRequestMapper.toUpdateUser(any(User.class), any(UserUpdateRequest.class))).thenReturn(user);
        when(userService.update(any(User.class))).thenReturn(user);

        mockMvc.perform(put(userEndpoint + "/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
        		.content(objectMapper.writeValueAsString(userUpdateRequest))) // Adjust JSON payload as needed
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		        .andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
				.andExpect(jsonPath("$.token").isEmpty()) // userData is null
				.andExpect(jsonPath("$.userData").isNotEmpty()); // userData is not null
        
        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdatePassword() throws Exception {
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRequestMapper.toUpdateUserPassword(any(User.class), any(PasswordUpdateRequest.class))).thenReturn(user);
        when(userService.updatePassword(any(User.class))).thenReturn(user);

        mockMvc.perform(patch(userEndpoint + "/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordUpdateRequest))) // Adjust JSON payload as needed
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
        		.andExpect(jsonPath("$.token").isEmpty()) // token is null
        		.andExpect(jsonPath("$.userData").isEmpty()); // userData is null

        verify(userService, times(1)).updatePassword(any(User.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDisableUser() throws Exception {
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(delete(userEndpoint + "/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
                .andExpect(jsonPath("$.successMessage").isNotEmpty()) // successMessage is not null
                .andExpect(jsonPath("$.token").isEmpty()) // token is null
        		.andExpect(jsonPath("$.userData").isEmpty()); // userData is not null

        verify(userService, times(1)).softDeleteUserById(anyLong());
    }
}
