package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.ExchangeRateConfig;
import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.config.RestTemplateConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.dto.ExternalAccountRequestMapper;
import com.fastcash.moneytransfer.dto.MoneyTransferRequest;
import com.fastcash.moneytransfer.dto.MoneyTransferRequestMapper;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.exception.UserAccountMismatchException;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.ExternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalAccountRepository;
import com.fastcash.moneytransfer.repository.InternalChargeAccountRepository;
import com.fastcash.moneytransfer.repository.MoneyTransferRepository;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.service.impl.ExchangeRateServiceImpl;
import com.fastcash.moneytransfer.util.PercentageChargeCalculator;
import com.fastcash.moneytransfer.util.UUIDTimestampTransactionIdGenerator;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@Import({ 
	RestTemplateConfig.class,
	ExchangeRateConfig.class,
	PasswordConfig.class,
	UserService.class, 
	MessageSourceConfig.class,
	MoneyTransferService.class,
	InternalMoneyTransferService.class,
	AccountService.class,
	InternalAccountService.class,
	UUIDTimestampTransactionIdGenerator.class,
	PercentageChargeCalculator.class,
	ExchangeRateServiceImpl.class,
	UserValidator.class,
	AccountStatementService.class,
	MoneyTransferRequestMapper.class,
	ExternalAccountService.class,
	InternalChargeAccountService.class,
	ExternalAccountRequestMapper.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class MoneyTransferServiceTest {
	
	@Autowired
	private MoneyTransferRepository moneyTransferRepository;

	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private InternalAccountService internalAccountService;
	
	@Autowired
	private InternalChargeAccountService internalChargeAccountService;
	
	@Autowired
    private UserAccountRepository userAccountRepository;
	
	@Autowired
    private ExternalAccountRepository externalAccountRepository;
	
	@Autowired
    private InternalAccountRepository internalAccountRepository;
	
	@Autowired
    private InternalChargeAccountRepository internalChargeAccountRepository;
	
	@Autowired
	private MoneyTransferService moneyTransferService;
	
	@Autowired
	private MoneyTransferRequestMapper moneyTransferRequestMapper;
	
	@MockBean
    private JavaMailSender javaMailSender;
	
	@Mock
    private TemplateEngine mockTemplateEngine;

	@MockBean
    private EmailNotifiable emailNotifiable;
	
	@Value("${app.admin.email}") 
	private String adminEmail;
	
	@Value("${app.admin.password}") 
	private String adminPassword;
	
	private User user;
	
	private List<UserAccount> userAccounts;
	
	@BeforeEach
	void setUp() {
		user = new User("test@user.com", adminPassword);
		entityManager.persist(user);
        entityManager.flush();
		
		userAccounts = accountService.create(user);
		user.setAccounts(userAccounts);
	}
	
	@Test
    void findByIdShouldThrowExceptionForUserAccountMismatch() throws InsufficientBalanceException {
		User debitUser = new User("user@example.com", "password");
        entityManager.persist(debitUser);
        
		UserAccount userAccount = new UserAccount(Currency.NGN, debitUser);
		
		MoneyTransfer moneyTransfer = new MoneyTransfer();
		moneyTransfer.setId(1L);
		moneyTransfer.setDebitAccount(userAccountRepository.save(userAccount));
		moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        moneyTransfer.setTransactionId("1713717611104-70eed7");
        moneyTransfer.setAmount(new BigDecimal("10.000"));
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setCreditCurrency(Currency.USD);
        moneyTransfer.setNotes("this is a test transfer");
        moneyTransfer.setCreditAccount(new UserAccount(Currency.USD, user));
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setTotalDebitedAmount(new BigDecimal("10.000"));
        moneyTransfer.setTotalCreditedAmount(new BigDecimal("10.000"));
		
        assertThrows(UserAccountMismatchException.class, () -> moneyTransferService.create(moneyTransfer, user));
    }
	
	@Test
	void testCreateOwnAccountMoneyTransfer() throws InsufficientBalanceException {
		BigDecimal amount = BigDecimal.TEN.setScale(Constants.AMOUNT_SCALE);
		
	     // Create input values
		UserAccount debitAccount = userAccounts.get(0);
		UserAccount creditAccount = userAccounts.get(1);
		
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create(
        	"OWN_ACCOUNT",
    		debitAccount.getId(), 
    		creditAccount.getId(), 
    		amount, 
    		debitAccount.getCurrency().toString(), 
    		creditAccount.getCurrency().toString(), 
    		BigDecimal.ONE, 
    		BigDecimal.TEN,
    		"this is a test transfer",
    		null,
    		null
        );
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
		
        moneyTransfer = moneyTransferService.create(moneyTransfer, user);
        
		assertEquals(TransactionType.OWN_ACCOUNT, moneyTransfer.getTransactionType());
		assertEquals(debitAccount, moneyTransfer.getDebitAccount());
		assertEquals(creditAccount, moneyTransfer.getCreditAccount());
		assertEquals(
			amount.multiply(new BigDecimal(-1)).setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(moneyTransfer.getDebitAccount().getId()).get().getBalance()
		);
        assertTrue(moneyTransferRepository.findById(moneyTransfer.getId()).isPresent());
	}
	
	@Test
	void testCreateExternalMoneyTransfer() throws InsufficientBalanceException {
		Admin admin = new Admin();
		admin.setEmail(adminEmail);
		admin.setPassword(adminPassword);
		admin.setRoles("ADMIN");	
		userService.create(admin);
		
        admin.setInternalAccounts(internalAccountService.create(admin));
    	admin.setChargeAccounts(internalChargeAccountService.create(admin));
		
        Long creditAccountId = 111111010101L;
		BigDecimal amount = BigDecimal.TEN.setScale(Constants.AMOUNT_SCALE);
		
	     // Create input values
		UserAccount debitAccount = userAccounts.get(0);
		
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create(
        	"INTER_BANK",
    		debitAccount.getId(), 
    		creditAccountId, 
    		amount, 
    		debitAccount.getCurrency().toString(),
    		"NGN", 
    		BigDecimal.ONE, 
    		BigDecimal.TEN,
    		"this is a test transfer",
    		"John Doe",
    		"New Bank"
        );
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
        moneyTransfer = moneyTransferService.create(moneyTransfer, user);
        
        ExternalAccount externalAccount = (ExternalAccount) moneyTransfer.getCreditAccount();
        
		assertEquals(TransactionType.INTER_BANK, moneyTransfer.getTransactionType());
		assertEquals(debitAccount, moneyTransfer.getDebitAccount());
		assertTrue(
			externalAccountRepository.findByAccountNumber(externalAccount.getId()).isPresent()
		);
		assertEquals(
			moneyTransfer.getTotalDebitedAmount().multiply(new BigDecimal(-1)).setScale(Constants.BALANCE_SCALE), 
			userAccountRepository.findById(moneyTransfer.getDebitAccount().getId()).get().getBalance()
		);
		assertEquals(
			moneyTransfer.getTotalCreditedAmount().setScale(Constants.BALANCE_SCALE), 
			internalAccountRepository.findById(moneyTransfer.getInternalAccount().getId()).get().getBalance()
		);
		assertEquals(
			moneyTransfer.getChargeAmount().setScale(Constants.BALANCE_SCALE), 
			internalChargeAccountRepository.findById(moneyTransfer.getInternalChargeAccount().getId()).get().getBalance()
		);
        assertTrue(moneyTransferRepository.findById(moneyTransfer.getId()).isPresent());
	}
}
