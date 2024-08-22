package com.fastcash.moneytransfer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.dto.APIResponse;
import com.fastcash.moneytransfer.dto.AccountRequest;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.repository.BaseUserRepository;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalChargeAccountRepository;
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
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
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
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;
    
    @MockBean
    private UserService userService;

    @MockBean
    private APIResponse controllerResponse;
    
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
    private AccountUpdateAuthorizationManager accountUpdateAuthorizationManager;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private final String accountEndpoint;
    
    private AccountRequest accountRequest;
    
	public AccountControllerTest(
    	@Value("${api.base.url}") String apiBaseUrl, 
    	@Value("${endpoint.account}") String accountEndpoint
    ) {
    	this.accountEndpoint = apiBaseUrl + accountEndpoint;
    }
	
	@BeforeEach
	void setUp() {
		 accountRequest = new AccountRequest(true);
	}
    
    @Test
    @WithMockUser("USER")
    void testUpdateAccount() throws Exception {
        // Given
        Long userId = 1L;
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);
        userAccount.setAllowOverdraft(false);
        
        User user = new User();
        
        when(accountService.findById(userId)).thenReturn(Optional.of(userAccount));
        when(accountService.update(any(UserAccount.class), eq(userService))).thenReturn(user);
        
        when(accountService.findById(userId)).thenReturn(Optional.of(userAccount));
        when(accountService.update(any(UserAccount.class), eq(userService))).thenReturn(user);
        when(userService.findUserByAccountId(userId)).thenReturn(user);
        when(accountUpdateAuthorizationManager.check(any(), any(RequestAuthorizationContext.class))).thenReturn(new AuthorizationDecision(true));


        // When & Then
    	mockMvc.perform(
    		MockMvcRequestBuilders.put(accountEndpoint + "/{id}", userId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(accountRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successMessage").isNotEmpty())
    		.andExpect(jsonPath("$.token").isEmpty())
    		.andExpect(jsonPath("$.userData").isNotEmpty());
    	
        verify(accountService).update(eq(userAccount), eq(userService));
    }

    @Test
    @WithMockUser("USER")
    void testUpdateAccountNotFound() throws Exception {
        // Given
        Long userId = 999L;
        
        when(accountService.findById(userId)).thenThrow(new ObjectNotFoundException("Account not found", userId));
        when(accountUpdateAuthorizationManager.check(any(), any(RequestAuthorizationContext.class))).thenReturn(new AuthorizationDecision(true));


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(accountEndpoint + "/{id}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest());
        
        verify(accountService, never()).update(any(UserAccount.class), eq(userService));
    }
}
