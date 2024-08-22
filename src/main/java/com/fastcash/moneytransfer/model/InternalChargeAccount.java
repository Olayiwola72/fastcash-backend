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
public class InternalChargeAccount extends TransactionAccount {
	
    @OneToMany(mappedBy = "internalChargeAccount", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference
    private List<MoneyTransfer> chargedTransfers;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	@JsonBackReference
	private Admin admin;

	public InternalChargeAccount() {
		super(); // Calls the default constructor of TransactionAccount
		this.setAccountCategory(AccountCategory.INTERNAL_ACCOUNT);
		this.setChargedTransfers(new ArrayList<>());
	}

	public InternalChargeAccount(Currency currency, Admin admin) {
		this(); // Calls the default constructor of InternalAccount
        this.setCurrency(currency);
        this.setAdmin(admin);
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
	public List<MoneyTransfer> getChargedTransfers() {
		return chargedTransfers;
	}

	public void setChargedTransfers(List<MoneyTransfer> chargedTransfers) {
		this.chargedTransfers = chargedTransfers;
	}
	
}