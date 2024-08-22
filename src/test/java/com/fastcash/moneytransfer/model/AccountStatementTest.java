package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;

class AccountStatementTest {
	
	private User user;
	
	@BeforeEach
	void setUp() {
		user = new User();
	}
	
	@Test
    void testAccountStatementInitialization() {
		// Arrange
		Admin admin = new Admin();
		
        Long id = 123L;
        String transactionId = "123";        
        BigDecimal amount = BigDecimal.valueOf(1000.50).setScale(Constants.AMOUNT_SCALE);
        String notes = "test transfer";
        Currency debitCurrency = Currency.USD;
        Currency creditCurrency = Currency.NGN;
        LocalDateTime createdAt = LocalDateTime.now();
        UserAccount debitAccount = new UserAccount(debitCurrency, user);
        UserAccount creditAccount = new UserAccount(creditCurrency, user);
        InternalAccount internalAccount = new InternalAccount(creditCurrency, admin);
        InternalChargeAccount internalChargeAccount = new InternalChargeAccount(creditCurrency, admin);
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
        moneyTransfer.setInternalAccount(internalAccount);
        moneyTransfer.setConversionRate(conversionRate);
        moneyTransfer.setTotalDebitedAmount(totalDebitedAmount);
        moneyTransfer.setTotalCreditedAmount(totalCreditedAmount);
        moneyTransfer.setTransactionType(transactionType);
        moneyTransfer.setChargeAmount(chargeAmount);
        moneyTransfer.setInternalChargeAccount(internalChargeAccount);
        moneyTransfer.setCreatedAt(createdAt);
        moneyTransfer.setVersion(version);
        
        User user = new User();    
        TransactionDirection transactionDirection = TransactionDirection.CREDIT; 
        
        // Act
		AccountStatement accountStatement = new AccountStatement(transactionDirection, moneyTransfer, user, debitAccount);

        // Assert
		assertNull(accountStatement.getId());
		assertEquals(debitAccount.getBalance(), accountStatement.getBalance());
        assertEquals(user, accountStatement.getUser());
        assertEquals(transactionDirection, accountStatement.getDirection());
        assertEquals(moneyTransfer, accountStatement.getMoneyTransfer());
        assertEquals(transactionId, accountStatement.getTransactionId());
        assertEquals(amount, accountStatement.getAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(notes, accountStatement.getNotes());
        assertEquals(debitCurrency, accountStatement.getDebitCurrency());
        assertEquals(creditCurrency, accountStatement.getCreditCurrency());
        assertEquals(debitAccount, accountStatement.getDebitAccount());
        assertEquals(creditAccount, accountStatement.getCreditAccount());
        assertEquals(internalAccount, accountStatement.getInternalAccount());
        assertEquals(conversionRate, accountStatement.getConversionRate());
        assertEquals(totalDebitedAmount, accountStatement.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(totalCreditedAmount, accountStatement.getTotalCreditedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(transactionType, accountStatement.getTransactionType());
        assertEquals(chargeAmount, accountStatement.getChargeAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(internalChargeAccount, accountStatement.getInternalChargeAccount());
        assertNotEquals(createdAt, accountStatement.getCreatedAt());
        assertFalse(accountStatement.isDeleted());
        assertNotNull(accountStatement.getVersion());
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
        Long id = 123L;
        User user = new User();    
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        TransactionDirection transactionDirection = TransactionDirection.DEBIT; 
        
        AccountStatement accountStatement = new AccountStatement();

        // Act
        accountStatement.setId(id);
        accountStatement.setUser(user);
        accountStatement.setDirection(transactionDirection);
        accountStatement.setMoneyTransfer(moneyTransfer);

        // Assert
        assertEquals(id, accountStatement.getId());
        assertEquals(user, accountStatement.getUser());
        assertEquals(transactionDirection, accountStatement.getDirection());
        assertEquals(moneyTransfer, accountStatement.getMoneyTransfer());
    }
}
