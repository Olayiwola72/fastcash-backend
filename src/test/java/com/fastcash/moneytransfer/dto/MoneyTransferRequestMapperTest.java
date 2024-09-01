package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.UserAccountRepository;
import com.fastcash.moneytransfer.repository.UserRepository;
import com.fastcash.moneytransfer.service.AccountService;
import com.fastcash.moneytransfer.service.EmailNotifiable;
import com.fastcash.moneytransfer.service.ExternalAccountService;
import com.fastcash.moneytransfer.service.impl.EmailNotificationService;
import com.fastcash.moneytransfer.service.impl.UUIDTimestampTransactionIdGenerator;

@DataJpaTest
@Import({
	MoneyTransferRequestMapper.class,
	UUIDTimestampTransactionIdGenerator.class,
	AccountService.class,
	ExternalAccountService.class,
	ExternalAccountRequestMapper.class,
	EmailNotificationService.class,
	TemplateEngine.class,
})
class MoneyTransferRequestMapperTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private UserAccountRepository userAccountRepository;
	
	@Autowired
	private MoneyTransferRequestMapper moneyTransferRequestMapper;

	@Autowired
    private UserRepository userRepository;
	
	@Mock
    private EmailNotifiable emailNotifiable;
	
	@MockBean
    private JavaMailSender mockMailSender;

    @Mock
    private TemplateEngine mockTemplateEngine;
	
	private User user;
	
	@BeforeEach
	void setUp() {
		user = new User("test@user.com", "password");
		entityManager.persist(user);
        entityManager.flush();
	}
	
	@AfterEach
	void tearDown() {
		userRepository.delete(user);
	}
	
	@Test
    void createMoneyTransferRequestMapper_OwnAccount() {
//         Create input values
        UserAccount debitAccount = new UserAccount(Currency.NGN, user);
        debitAccount = userAccountRepository.save(debitAccount);
        
        UserAccount creditAccount = new UserAccount(Currency.NGN, user);
        creditAccount = userAccountRepository.save(creditAccount);
        
        BigDecimal amount = BigDecimal.TEN.setScale(Constants.AMOUNT_SCALE);
        String debitCurrency = "NGN";
        String creditCurrency = "USD";
        String notes = "this is a test transfer";
        
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("OWN_ACCOUNT",debitAccount.getId(), creditAccount.getId(), amount, debitCurrency, creditCurrency, BigDecimal.ONE, BigDecimal.TEN, notes, null, null);
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
        
        // Assert that the instance is created successfully
        assertNotNull(moneyTransfer);
        assertEquals(moneyTransfer.getDebitedUser().getId(), user.getId());
        assertEquals(moneyTransfer.getAmount().setScale(Constants.AMOUNT_SCALE), request.amount());
        assertNotNull(moneyTransfer.getTransactionId());
        assertEquals(moneyTransfer.getDebitAccount().getId(), request.debitAccount());
        assertEquals(((UserAccount) moneyTransfer.getCreditAccount()).getId(), request.creditAccount());
        assertEquals(moneyTransfer.getDebitCurrency().toString(), request.debitCurrency());
        assertEquals(moneyTransfer.getCreditCurrency().toString(), request.creditCurrency());
        assertEquals(moneyTransfer.getNotes(), request.notes());
        assertEquals(moneyTransfer.getTransactionType(), TransactionType.OWN_ACCOUNT);
        assertEquals(moneyTransfer.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE), request.amount());
        assertEquals(moneyTransfer.getConversionRate(), request.conversionRate());
        assertEquals(moneyTransfer.getTotalCreditedAmount(), request.conversionAmount().setScale(Constants.AMOUNT_SCALE));
    }
	
	@Test
    void createMoneyTransferRequestMapper_InterBank() {
        // Create input values
        UserAccount debitAccount = new UserAccount(Currency.NGN, user);
        debitAccount = userAccountRepository.save(debitAccount);
        
        Long creditAccount = 0L;
        BigDecimal amount = BigDecimal.TEN.setScale(Constants.AMOUNT_SCALE);
        String debitCurrency = "NGN";
        String creditCurrency = "USD";
        String notes = "this is a test transfer";
        
        // Create MoneyTransferRequest instance using the static factory method
        MoneyTransferRequest request = MoneyTransferRequest.create("INTER_BANK",debitAccount.getId(), creditAccount, amount, debitCurrency, creditCurrency,  BigDecimal.ONE, BigDecimal.TEN, notes, "John Doe", "Bank Name");
        
        MoneyTransfer moneyTransfer = moneyTransferRequestMapper.toMoneyTransfer(user, request);
        
        // Assert that the instance is created successfully
        assertNotNull(moneyTransfer);
        assertEquals(moneyTransfer.getDebitedUser().getId(), user.getId());
        assertEquals(moneyTransfer.getAmount().setScale(Constants.AMOUNT_SCALE), request.amount());
        assertNotNull(moneyTransfer.getTransactionId());
        assertEquals(moneyTransfer.getDebitAccount().getId(), request.debitAccount());
        assertEquals(((ExternalAccount) moneyTransfer.getCreditAccount()).getId(), request.creditAccount());
        assertEquals(moneyTransfer.getDebitCurrency().toString(), request.debitCurrency());
        assertEquals(moneyTransfer.getCreditCurrency().toString(), request.creditCurrency());
        assertEquals(moneyTransfer.getNotes(), request.notes());
        assertEquals(moneyTransfer.getTransactionType(), TransactionType.INTER_BANK);
        assertEquals(moneyTransfer.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE), request.amount());
        assertEquals(moneyTransfer.getConversionRate(), request.conversionRate());
        assertEquals(moneyTransfer.getTotalCreditedAmount(), request.conversionAmount().setScale(Constants.AMOUNT_SCALE));
    }
	
}
