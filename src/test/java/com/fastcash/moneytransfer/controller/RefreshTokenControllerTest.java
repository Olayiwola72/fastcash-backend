package com.fastcash.moneytransfer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.dto.TokenRefreshRequest;
import com.fastcash.moneytransfer.exception.TokenRefreshException;
import com.fastcash.moneytransfer.model.RefreshToken;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.repository.BaseUserRepository;
import com.fastcash.moneytransfer.repository.FailedNotificationRepository;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalChargeAccountRepository;
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
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.RefreshTokenService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RefreshTokenController.class)
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
	InternalAccountService.class,
	InternalChargeAccountService.class,
	UserTypeChecker.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	EmailNotificationService.class,
})
class RefreshTokenControllerTest {
	
	@Value("${endpoint.auth}")
	private String authEndpoint;
	
	@Value("${endpoint.token.refresh}")
	private String tokenRefreshEndpoint;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @InjectMocks
    private RefreshTokenController refreshTokenController;
    
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private UserAccountRepository userAccountRepository;
    
    @MockBean
    private AdminRepository adminRepository;
    
    @MockBean
    private BaseUserRepository baseUserRepository;
    
    @MockBean
    private TransactionAccountRepository transactionAccountRepository;
    
    @MockBean
    private InternalAccountRepository internalAccountRepository;
    
    @MockBean
    private InternalChargeAccountRepository internalChargeAccountRepository;
    
    @MockBean
    private FailedNotificationRepository failedNotificationRepository;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ApiProperties apiProperties;
    
    private User user;
    private RefreshToken refreshToken;
    private String token;
    private String refreshTokenValue;
    private final String userAgent = "userAgent";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        token = UUID.randomUUID().toString();
        refreshTokenValue = UUID.randomUUID().toString();
        user = new User();
        user.setEmail("user@example.com");
        refreshToken = new RefreshToken(refreshTokenValue, LocalDateTime.now().plusMinutes(60), user, userAgent);
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        when(refreshTokenService.findByToken(refreshTokenValue)).thenReturn(refreshToken);
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class))).thenReturn(refreshToken);
        when(tokenAuthenticationService.authenticateUser(any(String.class), any(HttpServletRequest.class))).thenReturn(mock(Authentication.class));
        when(tokenAuthenticationService.generateToken(any(Authentication.class), eq(null))).thenReturn(token);

        TokenRefreshRequest request = new TokenRefreshRequest(refreshTokenValue);

        mockMvc.perform(post(apiProperties.fullTokenRefreshPath())  // Adjust the URL as per your configuration
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.refreshToken").value(refreshTokenValue));
    }
    
    @Test
    void testRefreshToken_TokenExpired() throws Exception {
        // Mock the behavior of the service
        RefreshToken refreshToken = new RefreshToken("a51afd5a-fe3a-43fd-90af-2cc4b3f0db51", LocalDateTime.now().minusDays(1), user, userAgent);

        when(refreshTokenService.findByToken(eq(refreshTokenValue))).thenReturn(refreshToken);
        when(refreshTokenService.verifyExpiration(eq(refreshToken))).thenThrow(new TokenRefreshException(refreshTokenValue, "Token has expired"));
        
        TokenRefreshRequest request = new TokenRefreshRequest(refreshTokenValue);

        mockMvc.perform(post(apiProperties.fullTokenRefreshPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage").isNotEmpty())
        		.andExpect(jsonPath("$.errors[0].fieldName").isNotEmpty());
    }


    @Test
    void testRefreshToken_TokenInvalid() throws Exception {
        when(refreshTokenService.findByToken(refreshTokenValue)).thenThrow(new TokenRefreshException(refreshTokenValue, "Token is invalid"));

        TokenRefreshRequest request = new TokenRefreshRequest(refreshTokenValue);

        mockMvc.perform(post(apiProperties.fullTokenRefreshPath())  // Adjust the URL as per your configuration
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage").isNotEmpty())
        		.andExpect(jsonPath("$.errors[0].fieldName").isNotEmpty());
    }
}

