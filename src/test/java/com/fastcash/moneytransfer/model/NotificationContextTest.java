package com.fastcash.moneytransfer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.util.DataMasker;

class NotificationContextTest {
	
	private final Long id = 0L;
	private final Long accountNumber = 0L;
    private final String accountHolderName = "John Doe";
    private final String bankName = "Test Bank";
    private final Currency currency = Currency.NGN;
	
	private User user;
	private UserAccount userAccount;
    private AccountStatement accountStatement;
    
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("test@email.com");
		user.setName("John Date");
		user.setLastLoginDate(new Date());
		
		userAccount = new UserAccount(Currency.USD, user);
		userAccount.setId(1L);
        userAccount.setBalance(new BigDecimal("5000.00"));
        
        ExternalAccount account = new ExternalAccount(currency, accountNumber, accountHolderName, bankName);
        
        accountStatement = new AccountStatement();
        accountStatement.setDirection(TransactionDirection.DEBIT);
        accountStatement.setTotalDebitedAmount(new BigDecimal("150.00"));
        accountStatement.setChargeAmount(BigDecimal.ZERO);
        accountStatement.setCreatedAt(LocalDateTime.now());
        accountStatement.setTransactionType(TransactionType.OWN_ACCOUNT);
        accountStatement.setNotes("Payment for services");
        accountStatement.setTransactionId("TX12345");
        accountStatement.setCreditAccount(account);
	}
	
	@Test
	void testDefaultConstructor() {
		NotificationContext notificationContext = new NotificationContext();
		
		assertNull(notificationContext.getId());
	}
	
	@Test
	void testUserConstructor(){
		NotificationContext notificationContext = new NotificationContext(NotificationType.EMAIL, user);
		
		assertNull(notificationContext.getId());
		assertEquals(user.getEmail(), notificationContext.getUser().getEmail());
		assertEquals(user.getName(), notificationContext.getName());
		assertEquals(user.getLastLoginDate(), notificationContext.getLastLoginDate());
	}
	
	@Test
	void testUserAndTokenConstructor(){
		String token = "token";
		NotificationContext notificationContext = new NotificationContext(NotificationType.EMAIL, user, token);
		
		assertNull(notificationContext.getId());
		assertEquals(user.getEmail(), notificationContext.getUser().getEmail());
		assertEquals(user.getName(), notificationContext.getName());
		assertEquals(user.getLastLoginDate(), notificationContext.getLastLoginDate());
		assertEquals(token, notificationContext.getToken());
	}
	
	@Test
	void testUserAndAccountConstructor(){
		NotificationContext notificationContext = new NotificationContext(NotificationType.EMAIL, user, userAccount);
		
		assertNull(notificationContext.getId());
		assertEquals(user.getEmail(), notificationContext.getUser().getEmail());
		assertEquals(user.getName(), notificationContext.getName());
		assertEquals(user.getLastLoginDate(), notificationContext.getLastLoginDate());
		assertEquals(DataMasker.maskAccountId(userAccount.getId().toString()),  notificationContext.getAccountId());
	}
	
	@Test
    void testUserAccountStatementConstructor() {
        NotificationContext notificationContext = new NotificationContext(NotificationType.EMAIL, user, userAccount, accountStatement);
        
        assertNull(notificationContext.getId());
        assertEquals(user.getEmail(), notificationContext.getUser().getEmail());
        assertEquals(user.getName(), notificationContext.getName());
        assertEquals(user.getLastLoginDate(), notificationContext.getLastLoginDate());
        
        assertEquals(userAccount.getCurrency() + " 5,000.00", notificationContext.getAccountBalance());
        assertEquals("Debit", notificationContext.getTransactionDirection());
        assertEquals(userAccount.getCurrency() + " 150.00", notificationContext.getTransactionAmount());
        
        // Since the charge amount is zero, transactionChargeAmount should be null
        assertNull(notificationContext.getTransactionChargeAmount());
        
        assertEquals(accountStatement.getTransactionType().getDescription(), notificationContext.getTransactionType());
        assertEquals(accountStatement.getNotes(), notificationContext.getTransactionNotes());
        assertEquals(accountStatement.getTransactionId(), notificationContext.getTransactionId());
        
        // Act: Simulate the logic where the credit account is an ExternalAccount
        if(accountStatement.getCreditAccount() instanceof ExternalAccount) {
            ExternalAccount account = (ExternalAccount) accountStatement.getCreditAccount();
            
            assertEquals(bankName, account.getBankName());
            assertEquals(accountHolderName, account.getAccountHolderName());
            assertEquals(accountNumber + " (" + currency + ")", account.getAccountNumber() + " (" + account.getCurrency() + ")");
        }
    }
	
	@Test
	void testGettersAndSetters() {
		NotificationContext notificationContext = new NotificationContext();
		
		notificationContext.setId(id);
		
		assertEquals(id, notificationContext.getId());
	}
	
}
