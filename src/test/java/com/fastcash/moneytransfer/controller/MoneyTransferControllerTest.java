package com.fastcash.moneytransfer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.PlatformTransactionManager;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.ApiProperties;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RestTemplateConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.dto.ExternalAccountRequestMapper;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.dto.MoneyTransferRequestMapper;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.AccountStatementRepository;
import com.fastcash.moneytransfer.repository.AdminRepository;
import com.fastcash.moneytransfer.repository.BaseUserRepository;
import com.fastcash.moneytransfer.repository.ExternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalChargeAccountRepository;
import com.fastcash.moneytransfer.repository.MoneyTransferRepository;
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
import com.fastcash.moneytransfer.service.AccountStatementService;
import com.fastcash.moneytransfer.service.ExternalAccountService;
import com.fastcash.moneytransfer.service.InternalAccountService;
import com.fastcash.moneytransfer.service.InternalChargeAccountService;
import com.fastcash.moneytransfer.service.InternalMoneyTransferService;
import com.fastcash.moneytransfer.service.MoneyTransferService;
import com.fastcash.moneytransfer.service.TokenAuthenticationService;
import com.fastcash.moneytransfer.service.UserService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.service.impl.ExchangeRateServiceImpl;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.PercentageChargeCalculator;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.util.UUIDTimestampTransactionIdGenerator;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;
import com.fastcash.moneytransfer.validation.ValidAccountValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MoneyTransferController.class)
@Import({
	RestTemplateConfig.class,
	MoneyTransferService.class,
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
	TemplateEngine.class,
	InternalMoneyTransferService.class,
	PercentageChargeCalculator.class,
	AccountStatementService.class,
	MoneyTransferRequestMapper.class,
	UUIDTimestampTransactionIdGenerator.class,
	ExchangeRateServiceImpl.class,
	ExternalAccountService.class,
	ExternalAccountRequestMapper.class
})
public class MoneyTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PlatformTransactionManager transactionManager;

    @MockBean
    private MoneyTransferService moneyTransferService;
    
    @MockBean
    private MoneyTransferRepository moneyTransferRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private MoneyTransferRequestMapper moneyTransferRequestMapper;

    @MockBean
    private ReloadableResourceBundleMessageSource messageSource;

    @InjectMocks
    private MoneyTransferController moneyTransferController;
    
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
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @MockBean
    private EmailNotificationService emailNotificationService;
    
    @MockBean
    private InternalAccountRepository internalAccountRepository;
    
    @MockBean
    private InternalChargeAccountRepository internalChargeAccountRepository;
    
    @MockBean
    private AccountStatementRepository accountStatementRepository;
    
    @MockBean
    private ExternalAccountRepository externalAccountRepository;
    
    @MockBean
    private ValidAccountValidator validAccountValidator;
    
    @Autowired
    private ApiProperties apiProperties;
    
	private User user;
	
	private MoneyTransfer moneyTransfer;
	
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Arrange
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        
        moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTransactionId("12345");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testCreateTransfer_Success() throws Exception {
        UserAccount debitAccount = new UserAccount(Currency.NGN, user);
        debitAccount.setId(10002L);
        debitAccount.setBalance(BigDecimal.TEN);

        UserAccount creditAccount = new UserAccount(Currency.NGN, user);
        creditAccount.setId(10003L);
        
        user.setAccounts(new ArrayList<>(List.of(debitAccount, creditAccount)));        
        
    	MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", debitAccount.getId(), creditAccount.getId(), BigDecimal.ONE, "NGN", "USD", BigDecimal.ONE, BigDecimal.ONE, "test transfer", null, null);
        
        when(userAccountRepository.findById(debitAccount.getId())).thenReturn(Optional.of(debitAccount));
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(moneyTransferRequestMapper.toMoneyTransfer(user, request)).thenReturn(moneyTransfer);
        when(moneyTransferService.create(moneyTransfer, user)).thenReturn(moneyTransfer);
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(messageSource.getMessage(eq("TransferSuccess"), any(), any())).thenReturn("Transfer successful");
        
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post(apiProperties.fullTransferPath()) // Use the actual endpoint here
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) // Adjust JSON payload as needed
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.successMessage").value("Transfer successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userData.email").value("user@example.com"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testCreateTransfer_InsufficientBalance() throws Exception {
        // Arrange
        UserAccount userAccount = new UserAccount(Currency.NGN, user);
        userAccount.setId(10002L);
        
        MoneyTransferRequest request = new MoneyTransferRequest("OWN_ACCOUNT", userAccount.getId(), 10002L, BigDecimal.TEN, "NGN", "USD",  BigDecimal.ONE, BigDecimal.TEN, "test transfer", null, null);
        
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(moneyTransferRequestMapper.toMoneyTransfer(user, request)).thenReturn(moneyTransfer);
        when(moneyTransferService.create(moneyTransfer, user)).thenThrow(new InsufficientBalanceException("Insufficient balance","InsufficientBalance", userAccount, request.amount(), "fieldName"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post(apiProperties.fullTransferPath()) // Use the actual endpoint here
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) // Adjust JSON payload as needed
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
}
