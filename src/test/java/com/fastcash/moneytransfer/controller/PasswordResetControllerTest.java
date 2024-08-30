package com.fastcash.moneytransfer.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.annotation.ApiBaseUrlPrefix;
import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.dto.ForgotPasswordRequest;
import com.fastcash.moneytransfer.dto.ResetPasswordRequest;
import com.fastcash.moneytransfer.dto.UserRequestMapper;
import com.fastcash.moneytransfer.model.PasswordResetToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.PasswordResetTokenRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DefaultUserDetailsChecker;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.PasswordResetTokenService;
import com.fastcash.moneytransfer.service.PasswordService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PasswordResetController.class)
@Import({
	PasswordResetTokenService.class,
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
	UserTypeChecker.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
	UserRequestMapper.class,
	PasswordService.class
})
class PasswordResetControllerTest {
	
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @MockBean
    private PasswordResetTokenRepository tokenRepository;
    
    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    private ApiProperties apiProperties;
    
    private final String email = "reset@email.com";
	private final String token = "token";
	private final String newPassword = "Password1$";
    
	private User user;
	
    @BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
		user = new User();
	}
	
	@Test
    void testControllerIsAnnotatedWithApiBaseUrlPrefix() {
        boolean isAnnotated = PasswordResetController.class.isAnnotationPresent(ApiBaseUrlPrefix.class);
        assertTrue(isAnnotated);
    }
	
	
	@Test
    void testForgotPassword() throws Exception {
        // Given
		when(userService.findByEmail(email)).thenReturn(Optional.of(user));
		
		ForgotPasswordRequest request = new ForgotPasswordRequest(email);
		
        // When & Then
    	mockMvc.perform(
    		MockMvcRequestBuilders.post(apiProperties.fullPasswordForgotPath())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successMessage").isNotEmpty())
    		.andExpect(jsonPath("$.token").isEmpty())
    		.andExpect(jsonPath("$.userData").isEmpty());
    }
	
	@Test
    void testResetPassword() throws Exception {
        // Given
		PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(Constants.PASSWORD_RESET_TOKEN_EXPIRY_HOURS));
		when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
		
		ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword);
		
        // When & Then
    	mockMvc.perform(
    		MockMvcRequestBuilders.post(apiProperties.fullPasswordResetPath())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successMessage").isNotEmpty())
    		.andExpect(jsonPath("$.token").isEmpty())
    		.andExpect(jsonPath("$.userData").isEmpty());
    }
}
