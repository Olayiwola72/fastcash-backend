package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RsaKeyConfig;
import com.fastcash.moneytransfer.config.SecurityConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AccountCategory;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.model.TransactionAccount;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.TransactionAccountRepository;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.security.AccountUpdateAuthorizationManager;
import com.fastcash.moneytransfer.security.DefaultUserDetailsChecker;
import com.fastcash.moneytransfer.security.DelegatedAuthenticationEntryPoint;
import com.fastcash.moneytransfer.security.DelegatedBearerTokenAccessDeniedHandler;
import com.fastcash.moneytransfer.security.InternalExternalUserFilter;
import com.fastcash.moneytransfer.security.TokenAuthenticationProvider;
import com.fastcash.moneytransfer.security.UserUpdateAuthorizationManager;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.KeyPairFileUtil;
import com.fastcash.moneytransfer.util.RSAKeyPairGenerator;
import com.fastcash.moneytransfer.util.TestConfig;
import com.fastcash.moneytransfer.validation.UserTypeChecker;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@Import({ 
	AccountService.class,
	EmailNotificationService.class,
	TemplateEngine.class,
	MessageSourceConfig.class,
	SecurityConfig.class,
	RsaKeyConfig.class,
	RSAKeyPairGenerator.class,
	KeyPairFileUtil.class,
	DelegatedAuthenticationEntryPoint.class, 
	DelegatedBearerTokenAccessDeniedHandler.class,
	PasswordConfig.class,
	UserService.class,
	UserValidator.class,
	InternalAccountService.class,
	InternalChargeAccountService.class,
	AccountUpdateAuthorizationManager.class,
	UserUpdateAuthorizationManager.class,
	InternalExternalUserFilter.class,
	TokenAuthenticationProvider.class,
	UserTypeChecker.class,
	DefaultUserDetailsChecker.class
})
class AccountServiceTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@Autowired
	private TransactionAccountRepository transactionAccountRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserService userService;
	
	@MockBean
    private JavaMailSender javaMailSender;
	
	@Mock
    private TemplateEngine mockTemplateEngine;

    @InjectMocks
    private EmailNotificationService emailNotificationService;
	
	private User user;
	
	@BeforeEach
    void setUp() {
        user = new User("user@example.com", "password");
        entityManager.persist(user);
        entityManager.flush();
    }
	
	@Test 
	void testFindById(){
		UserAccount userAccount = new UserAccount(Currency.NGN, user);
		userAccount = userAccountRepository.save(userAccount);
		
		userAccount = accountService.findById(userAccount.getId()).get();
		
		assertNotNull(userAccount);
		assertNotNull(userAccount.getId());
		assertEquals(BigDecimal.ZERO.setScale(Constants.BALANCE_SCALE), userAccount.getBalance());
		assertTrue(userAccount.isAllowOverdraft());
		assertNotNull(userAccount.getCreatedAt());
		assertEquals(Currency.NGN, userAccount.getCurrency());
		assertEquals(AccountCategory.USER_ACCOUNT, userAccount.getAccountCategory());
		assertFalse(userAccount.isDeleted());
	}
	
	@Test 
	void testCreate(){
		List<UserAccount> createdUserAccounts = accountService.create(user);
	    
	    // Assert that the number of created accounts matches the number of currencies
	    assertEquals(Currency.values().length, createdUserAccounts.size());
	    
	    // Retrieve all user accounts from the repository
	    List<UserAccount> allUserAccounts = userAccountRepository.findAll();
	    
	    // Assert that each created account is present in the repository
	    for (UserAccount createdAccount : createdUserAccounts) {
	        assertTrue(allUserAccounts.contains(createdAccount), "User account should be present in the repository");
	    }
	}
	
	@Test 
	void testUpdate(){
		Boolean allowOverdraft = false;
		UserAccount userAccount = new UserAccount(Currency.NGN, user);
		userAccount.setAllowOverdraft(true);
		userAccount = userAccountRepository.save(userAccount);
		user.setAccounts(new ArrayList<>(List.of(userAccount)));
		
		userAccount.setAllowOverdraft(allowOverdraft);
		User updatedUser = accountService.update(userAccount, userService);
		int index = updatedUser.getAccounts().indexOf(userAccount);
		
	    // Assert that the number of created accounts matches the number of currencies
	    assertEquals(allowOverdraft, updatedUser.getAccounts().get(index).isAllowOverdraft());
	}
	
	@Test 
	void testTransferFunds() throws InsufficientBalanceException{
		BigDecimal debitAmount = BigDecimal.ONE;
		BigDecimal creditAmount = BigDecimal.TEN;
		
		UserAccount fromAccount = new UserAccount(Currency.NGN, user);
		fromAccount = userAccountRepository.save(fromAccount);
		
		TransactionAccount toAccount = new UserAccount(Currency.NGN, user);
		toAccount = transactionAccountRepository.save(toAccount);
		
		accountService.transferFunds(fromAccount, toAccount, debitAmount, creditAmount);
		
		assertEquals(
			debitAmount.multiply(new BigDecimal(-1)).setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(fromAccount.getId()).get().getBalance()
		);
		
		assertEquals(
			creditAmount.setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(toAccount.getId()).get().getBalance()
		);
	}
	
	@Test 
	void testThrowInsufficientBalanceException_Withdraw() {
		UserAccount fromAccount = new UserAccount(Currency.NGN, user);
		fromAccount.setAllowOverdraft(false);
		userAccountRepository.save(fromAccount);
		
		// Call accountService withraw method
        assertThrows(InsufficientBalanceException.class, () -> accountService.withdraw(fromAccount, BigDecimal.ONE));
	}
	
	@Test 
	void testDeposit() {
		BigDecimal creditAmount = BigDecimal.TEN;
		
		TransactionAccount toAccount = new UserAccount(Currency.NGN, user);
		toAccount = transactionAccountRepository.save(toAccount);
		
		
		accountService.deposit(toAccount, creditAmount);
		
		assertEquals(
			creditAmount.setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(toAccount.getId()).get().getBalance()
		);
	}
	
	@Test 
	void testWithraw() {
		BigDecimal debitAmount = BigDecimal.TEN;
		
		UserAccount fromAccount = new UserAccount(Currency.NGN, user);
		fromAccount = userAccountRepository.save(fromAccount);
		
		accountService.withdraw(fromAccount, debitAmount);
		
		assertEquals(
			debitAmount.multiply(new BigDecimal(-1)).setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(fromAccount.getId()).get().getBalance()
		);
	}
}
