package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.enums.UserType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.PasswordService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.DateFormatter;

@DataJpaTest
@Import({
    UserRequestMapper.class,
    AccountService.class,
    PasswordService.class,
    EmailNotificationService.class,
	TemplateEngine.class,
	MessageSourceConfig.class
})
class UserResponseTest {
	
	@Autowired
    private UserRequestMapper requestMapper;
	
	@InjectMocks
	private DateFormatter dateFormatter;
	
	@Mock
    private MessageSource messageSource;
	
	@MockBean
    private JavaMailSender javaMailSender;

    private User user;
    private Admin admin;

    @BeforeEach
    void setUp() {
    	LocaleContextHolder.setLocale(Locale.US);
    	when(messageSource.getMessage("date.at", null, LocaleContextHolder.getLocale())).thenReturn("at");
    	
        // Set up User instance
        user = new User();
        user.setId(1L);
        user.setEmail("test@moneytransfer.com");
        user.setName("John Doe");
        user.setEnabled(true);
        user.setRoles("USER");
        user.setAuthMethod(AuthMethod.LOCAL);
        user.setCreatedAt(LocalDateTime.now());
        user.setUserType(UserType.INTERNAL);
        user.setVersion(1);
        user.setAccounts(Arrays.asList(new UserAccount()));
        user.setFamilyName("Doe");
        user.setGivenName("John");
        user.setPictureUrl("http://picture.url.jpg");
        user.setDefaultPassword(true);
        user.setEmailVerified(true);
        user.setExternalUserId("111222333");
        user.setLastLoginDate(new Date());
        
    	Long id = 123L;
        String transactionId = "123";        
        BigDecimal amount = BigDecimal.valueOf(1000.50).setScale(Constants.AMOUNT_SCALE);
        String notes = "test transfer";
        Currency debitCurrency = Currency.USD;
        Currency creditCurrency = Currency.NGN;
        LocalDateTime createdAt = LocalDateTime.now();
        UserAccount debitAccount = new UserAccount(debitCurrency, user);
        UserAccount creditAccount = new UserAccount(creditCurrency, user);
        BigDecimal conversionRate = BigDecimal.ONE;
        BigDecimal totalDebitedAmount = BigDecimal.valueOf(1.50).setScale(Constants.AMOUNT_SCALE);
        BigDecimal totalCreditedAmount = BigDecimal.valueOf(2.50).setScale(Constants.AMOUNT_SCALE);
        BigDecimal chargeAmount = BigDecimal.valueOf(0.05).setScale(Constants.AMOUNT_SCALE);
        TransactionType transactionType = TransactionType.INTER_BANK; 
        int version = 1;
        
        MoneyTransfer moneyTransfer = new MoneyTransfer();

        moneyTransfer.setId(id);
        moneyTransfer.setTransactionId(transactionId);
        moneyTransfer.setAmount(amount);
        moneyTransfer.setNotes(notes);
        moneyTransfer.setDebitCurrency(debitCurrency);
        moneyTransfer.setCreditCurrency(creditCurrency);
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setCreditAccount(creditAccount);
        moneyTransfer.setConversionRate(conversionRate);
        moneyTransfer.setTotalDebitedAmount(totalDebitedAmount);
        moneyTransfer.setTotalCreditedAmount(totalCreditedAmount);
        moneyTransfer.setTransactionType(transactionType);
        moneyTransfer.setChargeAmount(chargeAmount);
        moneyTransfer.setCreatedAt(createdAt);
        moneyTransfer.setVersion(version);
        
        TransactionDirection transactionDirection = TransactionDirection.CREDIT; 
        
        // Act
		AccountStatement accountStatement = new AccountStatement(transactionDirection, moneyTransfer, user, debitAccount);
		
        user.setTransfers(Arrays.asList(moneyTransfer));
        user.setAccountStatements(Arrays.asList(accountStatement));

        // Set up Admin instance
        admin = new Admin();
        admin.setId(2L);
        admin.setEmail("admin@moneytransfer.com");
        admin.setName("Admin User");
        admin.setEnabled(true);
        admin.setRoles("ADMIN");
        admin.setAuthMethod(AuthMethod.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUserType(UserType.INTERNAL);
        admin.setVersion(1);
        
        InternalAccount internalAccount = new InternalAccount(creditCurrency, admin);
        InternalChargeAccount internalChargeAccount = new InternalChargeAccount(creditCurrency, admin);
        moneyTransfer.setInternalAccount(internalAccount);
        moneyTransfer.setInternalChargeAccount(internalChargeAccount);
        
        admin.setInternalAccounts(Arrays.asList(internalAccount));
        admin.setChargeAccounts(Arrays.asList(internalChargeAccount));
        admin.setAccountStatements(Arrays.asList(accountStatement));
    }

    @Test
    void testUserResponseWithUser() {
        UserResponse userResponse = new UserResponse(user);

        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getName(), userResponse.getName());
        assertEquals(user.isEnabled(), userResponse.isEnabled());
        assertEquals(user.getRoles(), userResponse.getRoles());
        assertEquals(user.getAuthMethod(), userResponse.getAuthMethod());
        assertEquals(user.getCreatedAt(), userResponse.getCreatedAt());
        assertEquals(user.getUserType(), userResponse.getUserType());
        assertEquals(user.getPreferredLanguage(), userResponse.getPreferredLanguage());
        assertEquals(user.getVersion(), userResponse.getVersion());
        assertEquals(user.getAccounts(), userResponse.getAccounts());
        assertEquals(user.getFamilyName(), userResponse.getFamilyName());
        assertEquals(user.getGivenName(), userResponse.getGivenName());
        assertEquals(user.getPictureUrl(), userResponse.getPictureUrl());
        assertEquals(user.isEmailVerified(), userResponse.isEmailVerified());
        assertEquals(user.isDefaultPassword(), userResponse.isDefaultPassword());
        assertEquals(user.getExternalUserId(), userResponse.getExternalUserId());
        assertEquals(user.getLastLoginDate(), userResponse.getLastLoginDate());
        assertTrue(userResponse.getTransfers().stream()
            .allMatch(transferResponse -> user.getTransfers().stream()
                .anyMatch(transfer -> transfer.getCreatedAt().equals(transferResponse.getCreatedAt()))));
        assertTrue(userResponse.getAccountStatements().stream()
            .allMatch(statementResponse -> user.getAccountStatements().stream()
                .anyMatch(statement -> statement.getCreatedAt().equals(statementResponse.getCreatedAt()))));
    }

    @Test
    public void testUserResponseWithAdmin() {
        UserResponse userResponse = new UserResponse(admin);

        assertEquals(admin.getId(), userResponse.getId());
        assertEquals(admin.getEmail(), userResponse.getEmail());
        assertEquals(admin.getName(), userResponse.getName());
        assertEquals(admin.isEnabled(), userResponse.isEnabled());
        assertEquals(admin.getRoles(), userResponse.getRoles());
        assertEquals(admin.getAuthMethod(), userResponse.getAuthMethod());
        assertEquals(admin.getCreatedAt(), userResponse.getCreatedAt());
        assertEquals(admin.getUserType(), userResponse.getUserType());
        assertEquals(admin.getPreferredLanguage(), userResponse.getPreferredLanguage());
        assertEquals(admin.getVersion(), userResponse.getVersion());
        assertEquals(admin.getInternalAccounts(), userResponse.getInternalAccounts());
        assertEquals(admin.getChargeAccounts(), userResponse.getChargeAccounts());
        assertTrue(userResponse.getAccountStatements().stream()
            .allMatch(statementResponse -> admin.getAccountStatements().stream()
                .anyMatch(statement -> statement.getCreatedAt().equals(statementResponse.getCreatedAt()))));
    }

    @Test
    void testGetAccountStatements_sortedDescendingByCreationDate() {
    	MoneyTransfer transfer1 = new MoneyTransfer();
        MoneyTransfer transfer2 = new MoneyTransfer();
        MoneyTransfer transfer3 = new MoneyTransfer();
        
        // Set creation dates
        transfer1.setCreatedAt(LocalDateTime.now().minusDays(3));
        transfer1.setTransactionType(TransactionType.INTER_BANK);
        transfer1.setAmount(BigDecimal.ONE);
        transfer1.setId(1L);
        
        transfer2.setCreatedAt(LocalDateTime.now().minusDays(2));
        transfer2.setTransactionType(TransactionType.INTER_BANK);
        transfer2.setId(2L);
        transfer2.setAmount(BigDecimal.ONE);
        
        transfer3.setCreatedAt(LocalDateTime.now().minusDays(1));
        transfer3.setTransactionType(TransactionType.INTER_BANK);
        transfer3.setId(3L);
        transfer3.setAmount(BigDecimal.ONE);
        
        // Create some AccountStatement objects with different creation dates
    	AccountStatement statement1 = new AccountStatement(TransactionDirection.CREDIT, transfer1, new User(), new UserAccount());
    	AccountStatement statement2 = new AccountStatement(TransactionDirection.DEBIT, transfer2, new User(), new UserAccount());
    	AccountStatement statement3 = new AccountStatement(TransactionDirection.CREDIT, transfer3, new User(), new UserAccount());
        
        // Set creation dates
    	statement1.setCreatedAt(LocalDateTime.now().minusDays(3));
    	statement1.setId(1L);
        
    	statement2.setCreatedAt(LocalDateTime.now().minusDays(2));
    	statement2.setId(2L);
        
    	statement3.setCreatedAt(LocalDateTime.now().minusDays(1));
    	statement3.setId(3L);

        // Create input values
        String email = "test@email.com";
        String password = "123456";

        // Create UserRequest instance using the static factory method
        UserRequest request = UserRequest.create(email, password);
        User user = requestMapper.toUser(request);
        
        // Arrange: create an instance of the class containing the method to be tested
        user.setAccountStatements(Arrays.asList(statement1, statement2, statement3));

        UserResponse userResponse = new UserResponse(user);

        List<AccountStatementResponse> sortedStatements = userResponse.getAccountStatements();

        // Assert: check if the transfers are sorted by creation date in descending order
        assertEquals(statement3.getId(), sortedStatements.get(0).getId()); // Newest transfer should be first
        assertEquals(statement2.getId(), sortedStatements.get(1).getId());
        assertEquals(statement1.getId(), sortedStatements.get(2).getId()); // Oldest transfer should be last
    }
}
