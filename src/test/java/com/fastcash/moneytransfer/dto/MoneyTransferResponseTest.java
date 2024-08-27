package com.fastcash.moneytransfer.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.Admin;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.User;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.util.DateFormatter;

class MoneyTransferResponseTest {
	
	@InjectMocks
	private DateFormatter dateFormatter;
	
	@Mock
    private MessageSource messageSource;

    private MoneyTransfer moneyTransfer;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	LocaleContextHolder.setLocale(Locale.US);
    	when(messageSource.getMessage("date.at", null, LocaleContextHolder.getLocale())).thenReturn("at");
    	
    	User user = new User();
    	Admin admin = new Admin();
    	
        moneyTransfer = new MoneyTransfer();
        moneyTransfer.setId(1L);
        moneyTransfer.setTransactionId("1713717611104-70eed7");
        moneyTransfer.setAmount(new BigDecimal("10.000"));
        moneyTransfer.setDebitCurrency(Currency.NGN);
        moneyTransfer.setCreditCurrency(Currency.USD);
        moneyTransfer.setDebitAccount(new UserAccount(Currency.NGN, user));
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
    }

    @Test
    void testMoneyTransferResponse() {
        MoneyTransferResponse response = new MoneyTransferResponse(moneyTransfer);

        assertEquals(moneyTransfer.getId(), response.getId());
        assertEquals(moneyTransfer.getTransactionId(), response.getTransactionId());
        assertEquals(moneyTransfer.getAmount(), response.getAmount());
        assertEquals(moneyTransfer.getDebitCurrency(), response.getDebitCurrency());
        assertEquals(moneyTransfer.getCreditCurrency(), response.getCreditCurrency());
        assertEquals(moneyTransfer.getDebitAccount(), response.getDebitAccount());
        assertEquals(moneyTransfer.getNotes(), response.getNotes());
        assertEquals(moneyTransfer.getCreditAccount(), response.getCreditAccount());
        assertEquals(moneyTransfer.getInternalAccount(), response.getInternalAccount());
        assertEquals(moneyTransfer.getConversionRate(), response.getConversionRate());
        assertEquals(moneyTransfer.getTotalDebitedAmount(), response.getTotalDebitedAmount());
        assertEquals(moneyTransfer.getTotalCreditedAmount(), response.getTotalCreditedAmount());
        assertEquals(moneyTransfer.getChargeAmount(), response.getChargeAmount());
        assertEquals(moneyTransfer.getInternalChargeAccount(), response.getInternalChargeAccount());
        assertEquals(moneyTransfer.getTransactionType(), response.getTransactionType());
        assertEquals(
        	moneyTransfer.getNotes() == null || moneyTransfer.getNotes().isEmpty() ? moneyTransfer.getTransactionType().getDescription() : moneyTransfer.getTransactionType().getDescription() + ", " + moneyTransfer.getNotes(), 
        	response.getNarration()
        );
        assertEquals(moneyTransfer.getCreatedAt(), response.getCreatedAt());
        assertNotNull(response.getCreatedAtFormatted());
    }

    @Test
    void testMoneyTransferResponseWithoutNotes() {
        moneyTransfer.setNotes("");
        MoneyTransferResponse response = new MoneyTransferResponse(moneyTransfer);
        
        assertEquals(moneyTransfer.getTransactionType().getDescription(), response.getNarration());
    }
}
