package com.fastcash.moneytransfer.model;

import com.fastcash.moneytransfer.enums.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ExternalAccount extends Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Long id;
	
	private String accountHolderName;

    @Column(nullable = true)
    private String bankName;
    
    public ExternalAccount() {
    	super(); // Calls the default constructor of Account
    }
    
    public ExternalAccount(Currency currency, Long accountNumber, String accountHolderName, String bankName) {
    	super(currency, accountNumber);
    	this.setBankName(bankName);
    	this.setAccountHolderName(accountHolderName);
    }
    
	public String getAccountHolderName() {
		return accountHolderName;
	}
	
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
    
    public Long getId() {
		return super.getAccountNumber() != null ? super.getAccountNumber() : id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
         this.bankName = bankName;
    }
}
