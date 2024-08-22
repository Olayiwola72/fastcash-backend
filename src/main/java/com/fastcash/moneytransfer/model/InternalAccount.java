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
public class InternalAccount extends TransactionAccount {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	@JsonBackReference
	private Admin admin;
	
    @OneToMany(mappedBy = "internalAccount", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonManagedReference
    private List<MoneyTransfer> externalTransfers;
	
	public InternalAccount() {
		super(); // Calls the default constructor of TransactionAccount
		this.setAccountCategory(AccountCategory.INTERNAL_ACCOUNT);
		this.setExternalTransfers(new ArrayList<>());
	}

	public InternalAccount(Currency currency, Admin admin) {
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
	
	public List<MoneyTransfer> getExternalTransfers() {
		return externalTransfers;
	}

	public void setExternalTransfers(List<MoneyTransfer> externalTransfers) {
		this.externalTransfers = externalTransfers;
	}
	
}