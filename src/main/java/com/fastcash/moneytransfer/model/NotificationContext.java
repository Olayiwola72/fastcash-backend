package com.fastcash.moneytransfer.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.NotificationType;
import com.fastcash.moneytransfer.enums.TransactionDirection;
import com.fastcash.moneytransfer.util.DataMasker;
import com.fastcash.moneytransfer.util.DateFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class NotificationContext {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private NotificationType notificationType;
	
	@ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private User user;
	
	private String name;
	
	private Date lastLoginDate;
	
	private String token;
	
	private String accountId;
	
	private String accountBalance;
	
	private String transactionDirection;
	
	private String transactionAmount;
	
	private String transactionChargeAmount;
	
	private String transactionDate;
	
	private String transactionType;
	
	private String transactionNotes;
	
	private String transactionId;
	
	private String transactionExternalBankName;
	
	private String transactionExternalAccountName;
	
	private String transactionExternalAccountIdAndCurrency;
	
	public NotificationContext() {
		
	}
	
	public NotificationContext(NotificationType notificationType, User user) {
		this.notificationType = notificationType;
		this.user = user;
		this.name = user.getName();
		this.lastLoginDate = user.getLastLoginDate();
	}
	
	public NotificationContext(NotificationType notificationType, User user, String token) {
		this(notificationType, user);
		this.token = token;
	}
	
	public NotificationContext(NotificationType notificationType, User user, TransactionAccount userAccount) {
		this(notificationType, user);
		this.accountId =  userAccount.getId().toString();
		this.accountBalance = userAccount.getCurrency() + " "+ String.format("%,.2f", userAccount.getBalance().setScale(Constants.AMOUNT_SCALE, RoundingMode.DOWN));
	}
	
	public NotificationContext(NotificationType notificationType, User user, TransactionAccount userAccount, AccountStatement accountStatement) {
		this(notificationType, user, userAccount);
		this.transactionDirection = accountStatement.getDirection().toString().toLowerCase();
		this.transactionAmount = userAccount.getCurrency() +" "+ (accountStatement.getDirection().equals(TransactionDirection.DEBIT) ?  
			 
			String.format("%,.2f", accountStatement.getTotalDebitedAmount()) 
				: 
			String.format("%,.2f", accountStatement.getTotalCreditedAmount())
		);
		this.transactionChargeAmount = accountStatement.getChargeAmount().compareTo(BigDecimal.ZERO) > 0 ? userAccount.getCurrency() +" "+ String.format("%,.2f", accountStatement.getChargeAmount()) : null;
		this.transactionDate = DateFormatter.formatDate(accountStatement.getCreatedAt());
		this.transactionType = accountStatement.getTransactionType().getDescription();
		this.transactionNotes = accountStatement.getNotes();
		this.transactionId = accountStatement.getTransactionId();	
		
		if(accountStatement.getCreditAccount() instanceof ExternalAccount) {
			ExternalAccount account = (ExternalAccount) accountStatement.getCreditAccount();
			this.transactionExternalBankName = account.getBankName();
			this.transactionExternalAccountName = account.getAccountHolderName();
			this.transactionExternalAccountIdAndCurrency =  account.getAccountNumber() + " (" + account.getCurrency() + ")";
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public String getName() {
		return name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public String getToken() {
		return token;
	}

	public String getAccountId() {
		return DataMasker.maskAccountId(accountId);
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public String getTransactionDirection() {
		return StringUtils.capitalize(transactionDirection);
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public String getTransactionChargeAmount() {
		return transactionChargeAmount;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public String getTransactionNotes() {
		return transactionNotes;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getTransactionExternalBankName() {
		return transactionExternalBankName;
	}

	public String getTransactionExternalAccountName() {
		return transactionExternalAccountName;
	}

	public String getTransactionExternalAccountIdAndCurrency() {
		return transactionExternalAccountIdAndCurrency;
	}

}
