package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.config.MessageSourceConfig;
import com.fastcash.moneytransfer.config.PasswordConfig;
import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.exception.InsufficientBalanceException;
import com.fastcash.moneytransfer.exception.MissingInternalAccountException;
import com.fastcash.moneytransfer.exception.MissingInternalChargeAccountException;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.ExternalAccountRepository;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.util.PercentageChargeCalculator;
import com.fastcash.moneytransfer.validation.UserValidator;

@DataJpaTest
@Import({ 
	PasswordConfig.class,
	UserService.class, 
	MessageSourceConfig.class,
	InternalMoneyTransferService.class,
	AccountService.class,
	InternalAccountService.class,
	UserValidator.class,
	PercentageChargeCalculator.class,
	InternalChargeAccountService.class,
	EmailNotificationService.class,
	TemplateEngine.class,
	ExternalAccountService.class
})
class InternalMoneyTransferServiceTest {
	
	@Autowired
    private UserService userService;
    
	@Autowired
    private InternalAccountService internalAccountService;
	
	@Autowired
	private InternalChargeAccountService internalChargeAccountService;
    
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private InternalMoneyTransferService internalMoneyTransferService;
    
    @Autowired
    private ExternalAccountRepository externalAccountRepository;
    
    @MockBean
    private JavaMailSender javaMailSender;
    
    @Mock
    private TemplateEngine mockTemplateEngine;

    @InjectMocks
    private EmailNotificationService emailNotificationService;
    
    @Value("${app.admin.email}") 
	private String adminEmail;
	
	@Value("${app.admin.password}") 
	private String adminPassword;
	
	private Admin admin;
	
	private User user;
	
	@BeforeEach
	void setUp() {
    	admin = new Admin();
		admin.setEmail(adminEmail);
		admin.setPassword(adminPassword);
		admin.setRoles("ADMIN");
		userService.create(admin);
		
		user = new User("user@example.com", "password");
		userService.create(user);
	}

    @Test
    public void testHandleInternalTransfer() throws InsufficientBalanceException {
        // Mock user
    	Currency debitCurrency = Currency.USD;
    	Currency creditCurrency = Currency.NGN;
    	BigDecimal amount = BigDecimal.TEN;
    	ExternalAccount externalAccount = new ExternalAccount(Currency.NGN, 1L, null, null);
    	
    	InternalAccount actualInteralAccount = null;
    	InternalChargeAccount actualInternalChargeAccount = null;
    	UserAccount debitAccount = new UserAccount(debitCurrency, user);
    	userAccountRepository.save(debitAccount);
    	admin.setInternalAccounts(internalAccountService.create(admin));
    	admin.setChargeAccounts(internalChargeAccountService.create(admin));
    	
        Admin adminUser = (Admin) userService.findAdminByEmail(adminEmail).get();
        
        for(InternalAccount userAccount : adminUser.getInternalAccounts()) {
        	if(userAccount.getCurrency().equals(creditCurrency)) {
        		actualInteralAccount = userAccount;
        	}
        }
        
        for(InternalChargeAccount userAccount : adminUser.getChargeAccounts()) {
        	if(userAccount.getCurrency().equals(debitCurrency)) {
        		actualInternalChargeAccount = userAccount;
        	}
        }
        
        // Create a money transfer object
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setDebitCurrency(debitCurrency);
        moneyTransfer.setCreditCurrency(creditCurrency);
        moneyTransfer.setTotalDebitedAmount(amount);
        moneyTransfer.setTotalCreditedAmount(amount);
        moneyTransfer.setCreditAccount(externalAccount);
        
        // Call handleInternalTransfer method
        MoneyTransfer internalMoneyTransfer = internalMoneyTransferService.handleInternalTransfer(debitAccount, moneyTransfer, admin);
        
        assertNotNull(internalMoneyTransfer);
        
        InternalAccount expectedInternalAccount = moneyTransfer.getInternalAccount();
        InternalChargeAccount extectedInternalChargeAccount = moneyTransfer.getInternalChargeAccount();
        
        // Verify that internal credit account balance was updated
        assertEquals(actualInteralAccount.getId(), internalMoneyTransfer.getInternalAccount().getId());
        assertEquals(amount.setScale(Constants.BALANCE_SCALE), expectedInternalAccount.getBalance());
        assertEquals(internalMoneyTransfer.getCreditCurrency(), expectedInternalAccount.getCurrency());
        
        // Verify that charge account balance was updated
        assertEquals(actualInternalChargeAccount.getId(), internalMoneyTransfer.getInternalChargeAccount().getId());
        assertEquals(
        	internalMoneyTransfer.getChargeAmount().setScale(Constants.BALANCE_SCALE),
        	extectedInternalChargeAccount.getBalance()
        );
        assertEquals(internalMoneyTransfer.getDebitCurrency(), extectedInternalChargeAccount.getCurrency());
        
        // Verify that the total debited amount is computed correctly 
        assertEquals(
        	amount.add(internalMoneyTransfer.getChargeAmount()).setScale(Constants.AMOUNT_SCALE),
        	internalMoneyTransfer.getTotalDebitedAmount()
        );
        
        // Verify that debit account balance was debited with the total debited amount
        assertEquals(
        	internalMoneyTransfer.getTotalDebitedAmount().multiply(new BigDecimal(-1)).setScale(Constants.BALANCE_SCALE), 
        	userAccountRepository.findById(moneyTransfer.getDebitAccount().getId()).get().getBalance()
        );
        
        // Verify that external account balance was saved
        assertTrue(externalAccountRepository.findByAccountNumber(externalAccount.getAccountNumber()).isPresent());
    }
    
    @Test
    void testMissingInternalAccountException_WithNullAccounts() {
    	// Mock user
    	admin.setChargeAccounts(internalChargeAccountService.create(admin));
    	
    	BigDecimal amount = BigDecimal.TEN;
    	BigDecimal chargeAmount = amount.multiply(new BigDecimal("0.05"));
    	
    	UserAccount debitAccount = new UserAccount(Currency.USD, user);
    	userAccountRepository.save(debitAccount);
        
        // Create a money transfer object
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setDebitCurrency(Currency.USD);
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setTotalDebitedAmount(amount);
        moneyTransfer.setTotalCreditedAmount(amount);
        moneyTransfer.setChargeAmount(chargeAmount);
        
        // Call handleInternalTransfer method
        assertThrows(MissingInternalAccountException.class, () -> internalMoneyTransferService.handleInternalTransfer(debitAccount, moneyTransfer, admin));
    }
    
    @Test
    void testMissingInternalChargeAccountException_WithNullAccounts() {
    	// Mock user
		admin.setInternalAccounts(internalAccountService.create(admin));
    	
    	BigDecimal amount = BigDecimal.TEN;
    	BigDecimal chargeAmount = amount.multiply(new BigDecimal("0.05"));
    	
    	UserAccount debitAccount = new UserAccount(Currency.USD, user);
    	userAccountRepository.save(debitAccount);
        
        // Create a money transfer object
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setDebitCurrency(Currency.USD);
        moneyTransfer.setCreditCurrency(Currency.NGN);
        moneyTransfer.setTotalDebitedAmount(amount);
        moneyTransfer.setTotalCreditedAmount(amount);
        moneyTransfer.setChargeAmount(chargeAmount);
        
        // Call handleInternalTransfer method
        assertThrows(MissingInternalChargeAccountException.class, () -> internalMoneyTransferService.handleInternalTransfer(debitAccount, moneyTransfer, admin));
    }
}
