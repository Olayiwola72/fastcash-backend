package com.fastcash.moneytransfer.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.ExternalAccount;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.repository.AccountStatementRepository;
import com.fastcash.moneytransfer.repository.MoneyTransferRepository;

@DataJpaTest
@Import({ 
	AccountStatementService.class,
})
class AccountStatementServiceTest {
	
	@Autowired
	private AccountStatementRepository accountStatementRepository;
	
	@Autowired
	private AccountStatementService accountStatementService;
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private MoneyTransferRepository moneyTransferRepository;
	
	private MoneyTransfer moneyTransfer;
	
	private User user;
	
	private UserAccount debitAccount;
	
	@BeforeEach
	void setUp() {
		Admin admin = new Admin();
		
		user = new User("user@example.com", "password");
        entityManager.persist(user);
        
        BigDecimal amount = BigDecimal.valueOf(1000.50).setScale(Constants.AMOUNT_SCALE);
        String notes = "test transfer";
        Currency debitCurrency = Currency.USD;
        Currency creditCurrency = Currency.NGN;
        LocalDateTime createdAt = LocalDateTime.now();
        debitAccount = new UserAccount(debitCurrency, user);
        InternalAccount internalAccount = new InternalAccount(creditCurrency, admin);
        InternalChargeAccount internalChargeAccount = new InternalChargeAccount(creditCurrency, admin);
        BigDecimal conversionRate = BigDecimal.ONE;
        BigDecimal totalDebitedAmount = BigDecimal.valueOf(1.50).setScale(Constants.AMOUNT_SCALE);
        BigDecimal totalCreditedAmount = BigDecimal.valueOf(2.50).setScale(Constants.AMOUNT_SCALE);
        BigDecimal chargeAmount = BigDecimal.valueOf(0.05).setScale(Constants.AMOUNT_SCALE);
        TransactionType transactionType = TransactionType.INTER_BANK; 
        int version = 1;
        
        entityManager.persist(debitAccount);
        
        moneyTransfer = new MoneyTransfer();
        moneyTransfer.setDebitedUser(user);
        moneyTransfer.setTransactionId("2");
        moneyTransfer.setAmount(amount);
        moneyTransfer.setNotes(notes);
        moneyTransfer.setDebitCurrency(debitCurrency);
        moneyTransfer.setCreditCurrency(creditCurrency);
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setInternalAccount(internalAccount);
        moneyTransfer.setConversionRate(conversionRate);
        moneyTransfer.setTotalDebitedAmount(totalDebitedAmount);
        moneyTransfer.setTotalCreditedAmount(totalCreditedAmount);
        moneyTransfer.setTransactionType(transactionType);
        moneyTransfer.setChargeAmount(chargeAmount);
        moneyTransfer.setInternalChargeAccount(internalChargeAccount);
        moneyTransfer.setCreatedAt(createdAt);
        moneyTransfer.setVersion(version);
        
	}
	
	@Test 
	void AccountStatementInitialization_Account(){
		UserAccount creditAccount = new UserAccount(moneyTransfer.getCreditCurrency(), user);
        entityManager.persist(creditAccount);
        entityManager.flush();
        
        moneyTransfer.setId(1L);
        moneyTransfer.setCreditAccount(creditAccount);
		moneyTransferRepository.save(moneyTransfer);
		
		AccountStatement accountStatement = new AccountStatement(TransactionDirection.DEBIT, moneyTransfer, user, debitAccount);
		AccountStatement result = accountStatementService.create(accountStatement);
		
		assertNotNull(result);
		assertNotNull(accountStatementRepository.findById(result.getId()).isPresent());
	}
	
	@Test 
	void AccountStatementInitialization_ExternalAccount(){
		ExternalAccount externalAccount = new ExternalAccount(moneyTransfer.getCreditCurrency(), 0L, null, null);
        entityManager.persist(externalAccount);
        entityManager.flush();
        
        moneyTransfer.setId(2L);
		moneyTransfer.setCreditAccount(externalAccount);
		moneyTransferRepository.save(moneyTransfer);
		
		AccountStatement accountStatement = new AccountStatement(TransactionDirection.DEBIT, moneyTransfer, user, debitAccount);
		AccountStatement result = accountStatementService.create(accountStatement);
		
		assertNotNull(result);
		assertNotNull(accountStatementRepository.findById(result.getId()).isPresent());
	}
}
