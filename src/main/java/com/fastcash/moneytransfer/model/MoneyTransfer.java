package com.fastcash.moneytransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.fastcash.moneytransfer.annotation.ValidMoneyTransfer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Represents a money transfer transaction.
 */
@Entity
@ValidMoneyTransfer
public class MoneyTransfer extends TransferDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "debited_user_id", nullable = false)
	@JsonBackReference
	private User debitedUser;
	
	@OneToMany(mappedBy="moneyTransfer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
    private List<AccountStatement> accountStatements;
    
    public MoneyTransfer() {
		this.setAccountStatements(new ArrayList<>());
	}
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public User getDebitedUser() {
		return debitedUser;
	}

	public void setDebitedUser(User debitedUser) {
		this.debitedUser = debitedUser;
	}
    
    public List<AccountStatement> getAccountStatements() {
		return accountStatements;
	}

	public void setAccountStatements(List<AccountStatement> accountStatements) {
		this.accountStatements = accountStatements;
	}
	
}