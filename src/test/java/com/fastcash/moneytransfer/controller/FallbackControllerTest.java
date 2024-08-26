package com.fastcash.moneytransfer.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
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
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@WebMvcTest(FallbackController.class)
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
	UserTypeChecker.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserUpdateAuthorizationManager.class,
	AccountUpdateAuthorizationManager.class,
	DefaultUserDetailsChecker.class,
	AccountService.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	EmailNotificationService.class,
})
public class FallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private BaseUserRepository baseUserRepository;
    
    @MockBean
    private AdminRepository adminRepository;
    
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    
    @MockBean
    private UserAccountRepository userAccountRepository;
    
    @MockBean
    private FailedNotificationRepository failedNotificationRepository;
    
    @MockBean
    private TransactionAccountRepository transactionAccountRepository;
    
    @MockBean
    private InternalAccountRepository internalAccountRepository;
    
    @MockBean
    private InternalChargeAccountRepository internalChargeAccountRepository;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;

    @Test
    public void testNonAssetRouteShouldForwardToIndexHtml() throws Exception {
        mockMvc.perform(get("/password/reset"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    public void testAssetRouteShouldNotForward() throws Exception {
        mockMvc.perform(get("/assets/somefile.js"))
                .andExpect(status().isNotFound()); // Assuming there's no such static file
    }

    @Test
    public void testRootRouteShouldForwardToIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    @Test
    public void testAssetWithExtensionShouldNotForward() throws Exception {
        mockMvc.perform(get("/main.css"))
                .andExpect(status().isNotFound()); // Assuming there's no such static file
    }

    @Test
    public void testHtmlFileShouldNotForward() throws Exception {
        mockMvc.perform(get("/somepage.html"))
                .andExpect(status().isNotFound()); // Assuming there's no such static file
    }
    
}
