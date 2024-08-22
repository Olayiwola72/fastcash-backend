package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;

class MoneyTransferTest {
	
	private User user;
	private Admin admin;
	
	@BeforeEach
	void setUp() {
		user = new User();
		admin = new Admin();
	}
	
	@Test
    void testMoneyTransferInitialization() {
        // Arrange
        // Act
        MoneyTransfer moneyTransfer = new MoneyTransfer();

        // Assert
        assertNull(moneyTransfer.getId()); // Id should be null
        assertNull(moneyTransfer.getDebitedUser());
        assertEquals(BigDecimal.ZERO.setScale(Constants.AMOUNT_SCALE), moneyTransfer.getChargeAmount()); // Initial balance should be zero
        assertEquals(0, moneyTransfer.getAccountStatements().size());
        assertNotNull(moneyTransfer.getCreatedAt()); // CreatedAt should not be null
        assertFalse(moneyTransfer.isDeleted()); // deleted should be false initially
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
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
        List<AccountStatement> accountStatements = List.of(new AccountStatement());
        
        MoneyTransfer moneyTransfer = new MoneyTransfer();

        // Act
        moneyTransfer.setId(id);
        moneyTransfer.setDebitedUser(user);
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
        moneyTransfer.setDeleted(true);
        moneyTransfer.setVersion(version);
        moneyTransfer.setAccountStatements(accountStatements);

        // Assert
        assertEquals(id, moneyTransfer.getId());
        assertEquals(user, moneyTransfer.getDebitedUser());
        assertEquals(transactionId, moneyTransfer.getTransactionId());
        assertEquals(amount, moneyTransfer.getAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(notes, moneyTransfer.getNotes());
        assertEquals(debitCurrency, moneyTransfer.getDebitCurrency());
        assertEquals(creditCurrency, moneyTransfer.getCreditCurrency());
        assertEquals(debitAccount, moneyTransfer.getDebitAccount());
        assertEquals(creditAccount, moneyTransfer.getCreditAccount());
        assertEquals(internalAccount, moneyTransfer.getInternalAccount());
        assertEquals(conversionRate, moneyTransfer.getConversionRate());
        assertEquals(totalDebitedAmount, moneyTransfer.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(totalCreditedAmount, moneyTransfer.getTotalCreditedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(transactionType, moneyTransfer.getTransactionType());
        assertEquals(chargeAmount, moneyTransfer.getChargeAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(internalChargeAccount, moneyTransfer.getInternalChargeAccount());
        assertEquals(createdAt, moneyTransfer.getCreatedAt());
        assertTrue(moneyTransfer.isDeleted());
        assertEquals(version, moneyTransfer.getVersion());
        assertEquals(accountStatements.size(), moneyTransfer.getAccountStatements().size());
    }
	
	@Test
    public void testSetAmountSetter() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(2000.50).setScale(Constants.AMOUNT_SCALE);
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        
        // Act
        moneyTransfer.setAmount(amount);
        
        // Assert
        assertEquals(amount, moneyTransfer.getAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(amount, moneyTransfer.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(amount, moneyTransfer.getTotalCreditedAmount().setScale(Constants.AMOUNT_SCALE));
    }
	
}
