package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;

class TransferDetailsTest {
	
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
		TransferDetails transferDetails = new TransferDetails();

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(Constants.AMOUNT_SCALE), transferDetails.getChargeAmount()); // Initial balance should be zero
        assertNotNull(transferDetails.getCreatedAt()); // CreatedAt should not be null
        assertFalse(transferDetails.isDeleted()); // deleted should be false initially
    }
	
	@Test
    public void testGettersAndSetters() {
        // Arrange
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
        
        TransferDetails transferDetails = new TransferDetails();

        // Act
        transferDetails.setTransactionId(transactionId);
        transferDetails.setAmount(amount);
        transferDetails.setNotes(notes);
        transferDetails.setDebitCurrency(debitCurrency);
        transferDetails.setCreditCurrency(creditCurrency);
        transferDetails.setDebitAccount(debitAccount);
        transferDetails.setCreditAccount(creditAccount);
        transferDetails.setInternalAccount(internalAccount);
        transferDetails.setConversionRate(conversionRate);
        transferDetails.setTotalDebitedAmount(totalDebitedAmount);
        transferDetails.setTotalCreditedAmount(totalCreditedAmount);
        transferDetails.setTransactionType(transactionType);
        transferDetails.setChargeAmount(chargeAmount);
        transferDetails.setInternalChargeAccount(internalChargeAccount);
        transferDetails.setCreatedAt(createdAt);
        transferDetails.setDeleted(true);
        transferDetails.setVersion(version);

        // Assert
        assertEquals(transactionId, transferDetails.getTransactionId());
        assertEquals(amount, transferDetails.getAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(notes, transferDetails.getNotes());
        assertEquals(debitCurrency, transferDetails.getDebitCurrency());
        assertEquals(creditCurrency, transferDetails.getCreditCurrency());
        assertEquals(debitAccount, transferDetails.getDebitAccount());
        assertEquals(creditAccount, transferDetails.getCreditAccount());
        assertTrue(transferDetails.getCreditAccount() instanceof UserAccount);
        assertEquals(internalAccount, transferDetails.getInternalAccount());
        assertEquals(conversionRate, transferDetails.getConversionRate());
        assertEquals(totalDebitedAmount, transferDetails.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(totalCreditedAmount, transferDetails.getTotalCreditedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(transactionType, transferDetails.getTransactionType());
        assertEquals(chargeAmount, transferDetails.getChargeAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(internalChargeAccount, transferDetails.getInternalChargeAccount());
        assertEquals(createdAt, transferDetails.getCreatedAt());
        assertTrue(transferDetails.isDeleted());
        assertEquals(version, transferDetails.getVersion());
    }
	
	@Test
    public void testGettersAndSetters_ExternalAccount() {
        // Arrange
        ExternalAccount externalAccount = new ExternalAccount(Currency.NGN, 1L, "John Doe", "test bank");
        TransferDetails transferDetails = new TransferDetails();

        // Act
        transferDetails.setCreditAccount(externalAccount);

        // Assert
        assertEquals(externalAccount, transferDetails.getCreditAccount());
        assertTrue(transferDetails.getCreditAccount() instanceof ExternalAccount);
    }
	
	@Test
    public void testSetAmountSetter() {
        // Arrange
        BigDecimal amount = BigDecimal.valueOf(2000.50).setScale(Constants.AMOUNT_SCALE);
        TransferDetails transferDetails = new TransferDetails();
        
        // Act
        transferDetails.setAmount(amount);
        
        // Assert
        assertEquals(amount, transferDetails.getAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(amount, transferDetails.getTotalDebitedAmount().setScale(Constants.AMOUNT_SCALE));
        assertEquals(amount, transferDetails.getTotalCreditedAmount().setScale(Constants.AMOUNT_SCALE));
    }
	
}
