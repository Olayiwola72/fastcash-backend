package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;

class AccountStatementResponseTest {

    private AccountStatement accountStatement;
    
    private UserAccount debitAccount;
    
    @BeforeEach
    void setUp() {
    	User user = new User();
    	Admin admin = new Admin();
    	
    	debitAccount = new UserAccount(Currency.NGN, user);
    	debitAccount.setBalance(BigDecimal.TEN);
    	
    	MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setId(1L);
        moneyTransfer.setTransactionId("1713717611104-70eed7");
        moneyTransfer.setAmount(new BigDecimal("10.000"));
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setCreditCurrency(Currency.USD);
        moneyTransfer.setDebitAccount(debitAccount);
        moneyTransfer.setNotes("this is a test transfer");
        moneyTransfer.setCreditAccount(new UserAccount(Currency.USD, user));
        moneyTransfer.setInternalAccount(new InternalAccount(Currency.USD, admin));
        moneyTransfer.setConversionRate(BigDecimal.ONE);
        moneyTransfer.setTotalDebitedAmount(new BigDecimal("10.000"));
        moneyTransfer.setTotalCreditedAmount(new BigDecimal("10.000"));
        moneyTransfer.setChargeAmount(new BigDecimal("0.5"));
        moneyTransfer.setInternalChargeAccount(new InternalChargeAccount(Currency.USD, admin));
        moneyTransfer.setTransactionType(TransactionType.OWN_ACCOUNT);
        moneyTransfer.setCreatedAt(LocalDateTime.of(2022, 4, 25, 10, 15, 30));
        
        accountStatement = new AccountStatement(TransactionDirection.DEBIT, moneyTransfer, new User(), debitAccount);
    }

    @Test
    void testAccountStatementResponse() {
    	AccountStatementResponse response = new AccountStatementResponse(accountStatement);

        assertEquals(accountStatement.getId(), response.getId());
        assertEquals(accountStatement.getBalance(), response.getBalance());
        assertEquals(accountStatement.getTransactionId(), response.getTransactionId());
        assertEquals(accountStatement.getAmount(), response.getAmount());
        assertEquals(accountStatement.getDebitCurrency(), response.getDebitCurrency());
        assertEquals(accountStatement.getCreditCurrency(), response.getCreditCurrency());
        assertEquals(accountStatement.getDebitAccount(), response.getDebitAccount());
        assertEquals(accountStatement.getNotes(), response.getNotes());
        assertEquals(accountStatement.getCreditAccount(), response.getCreditAccount());
        assertEquals(accountStatement.getInternalAccount(), response.getInternalAccount());
        assertEquals(accountStatement.getConversionRate(), response.getConversionRate());
        assertEquals(accountStatement.getTotalDebitedAmount(), response.getTotalDebitedAmount());
        assertEquals(accountStatement.getTotalCreditedAmount(), response.getTotalCreditedAmount());
        assertEquals(accountStatement.getChargeAmount(), response.getChargeAmount());
        assertEquals(accountStatement.getInternalChargeAccount(), response.getInternalChargeAccount());
        assertEquals(accountStatement.getTransactionType(), response.getTransactionType());
        assertEquals(
        		accountStatement.getNotes() == null || accountStatement.getNotes().isEmpty() ? accountStatement.getTransactionType().getDescription() : accountStatement.getTransactionType().getDescription() + ", " + accountStatement.getNotes(), 
        	response.getNarration()
        );
        assertEquals(accountStatement.getDirection().getDescription(), response.getDirection());
        assertEquals(accountStatement.getDirection().getSign(), response.getSign());
        assertEquals(accountStatement.getCreatedAt(), response.getCreatedAt());
        assertNotNull(response.getCreatedAtFormatted());
    }

    @Test
    void testAccountStatementResponseWithoutNotes() {
    	accountStatement.setNotes("");
        AccountStatementResponse response = new AccountStatementResponse(accountStatement);
        
        assertEquals(accountStatement.getTransactionType().getDescription(), response.getNarration());
    }
}
