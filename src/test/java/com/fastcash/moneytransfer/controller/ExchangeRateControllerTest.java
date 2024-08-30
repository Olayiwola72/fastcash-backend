package com.fastcash.moneytransfer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.dto.ExchangeRateResponse;
import com.fastcash.moneytransfer.enums.ExchangeRateApiResponseType;
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
import com.fastcash.moneytransfer.service.ExchangeRateService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@WebMvcTest(ExchangeRateController.class)
@Import({
    MessageSourceConfig.class,
    ExchangeRateService.class,
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
    
})
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private AdminRepository adminRepository;
    
    @MockBean
    private BaseUserRepository baseUserRepository;
    
    @MockBean
    private UserAccountRepository userAccountRepository;
    
    @MockBean
    private TransactionAccountRepository transactionAccountRepository;
    
    @MockBean
    private InternalAccountRepository internalAccountRepository;
    
    @MockBean
    private InternalChargeAccountRepository internalChargeAccountRepository;
    
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    
    @MockBean
    private FailedNotificationRepository failedNotificationRepository;
    
    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;
    
    @Autowired
    private ApiProperties apiProperties;
    
    
    private ExchangeRateResponse mockExchangeRateResponse;
    private ExchangeRateResponse mockExchangeAmountResponse;
    
    private final String baseCurrency = "NGN";
    private final String targetCurrency = "USD";
    private final String successMessage = ExchangeRateApiResponseType.success.toString();
    private final BigDecimal conversionRate = BigDecimal.valueOf(0.8412);
    private final BigDecimal conversionResult = BigDecimal.ONE;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize common test variables
        mockExchangeRateResponse = new ExchangeRateResponse(successMessage, baseCurrency, targetCurrency, conversionRate, null, null);
        mockExchangeAmountResponse = new ExchangeRateResponse(successMessage, baseCurrency, targetCurrency, conversionRate, conversionResult, null);
    }

    @Test
    @WithMockUser()
    void testGetExchangeRateWithoutAmount() throws Exception {
        // Given
        when(exchangeRateService.getExchangeRate(anyString(), anyString())).thenReturn(mockExchangeRateResponse);

        // When / Then
        mockMvc.perform(get(apiProperties.fullExchangeRatePath())
                .param("baseCurrency", baseCurrency)
                .param("targetCurrency", targetCurrency)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value(successMessage))
                .andExpect(jsonPath("$.base_code").value(baseCurrency))
                .andExpect(jsonPath("$.target_code").value(targetCurrency))
                .andExpect(jsonPath("$.conversion_rate").value(conversionRate.doubleValue()))
                .andExpect(jsonPath("$.conversion_result").doesNotExist()); // Ensure conversion_result is null
    }

    @Test
    @WithMockUser()
    void testGetExchangeRateWithAmount() throws Exception {
        // Given
        BigDecimal amount = BigDecimal.TEN;
        when(exchangeRateService.getExchangeAmount(anyString(), anyString(), any(BigDecimal.class))).thenReturn(mockExchangeAmountResponse);

        // When / Then
        mockMvc.perform(get(apiProperties.fullExchangeRatePath())
                .param("baseCurrency", baseCurrency)
                .param("targetCurrency", targetCurrency)
                .param("amount", amount.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value(successMessage))
                .andExpect(jsonPath("$.base_code").value(baseCurrency))
                .andExpect(jsonPath("$.target_code").value(targetCurrency))
                .andExpect(jsonPath("$.conversion_rate").value(conversionRate.doubleValue()))
                .andExpect(jsonPath("$.conversion_result").value(conversionResult)); // Ensure conversion_result is 1
    }
}

