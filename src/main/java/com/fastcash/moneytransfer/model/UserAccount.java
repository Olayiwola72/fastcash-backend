package com.fastcash.moneytransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.fastcash.moneytransfer.enums.AccountCategory;
import com.fastcash.moneytransfer.enums.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class UserAccount extends TransactionAccount {
	
	@OneToMany(mappedBy = "debitAccount", fetch = FetchType.LAZY)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonManagedReference
    private List<MoneyTransfer> debitTransfers;

    @OneToMany(mappedBy = "creditInternalAccount", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference
    private List<MoneyTransfer> creditTransfers;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private User user;
	
    private boolean deleted = Boolean.FALSE;

    public UserAccount() {
    	super(); // Calls the default constructor of TransactionAccount
		this.setAccountCategory(AccountCategory.USER_ACCOUNT);
		this.setDebitTransfers(new ArrayList<>());
		this.setCreditTransfers(new ArrayList<>());
    }
    
	public UserAccount(Currency currency, User user) {
		this();
		this.setCurrency(currency);
		this.setUser(user);
	}
	
	public List<MoneyTransfer> getDebitTransfers() {
		return debitTransfers;
	}

	public void setDebitTransfers(List<MoneyTransfer> debitTransfers) {
		this.debitTransfers = debitTransfers;
	}

	public List<MoneyTransfer> getCreditTransfers() {
		return creditTransfers;
	}

	public void setCreditTransfers(List<MoneyTransfer> creditTransfers) {
		this.creditTransfers = creditTransfers;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
