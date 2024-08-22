package com.fastcash.moneytransfer.model;

import java.util.ArrayList;
import java.util.List;

import com.fastcash.moneytransfer.annotation.ValidAdmin;
import com.fastcash.moneytransfer.enums.AuthMethod;
import com.fastcash.moneytransfer.enums.UserType;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("ADMIN")
@ValidAdmin
public class Admin extends BaseUser {
	
	@OneToMany(mappedBy="admin", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<InternalAccount> internalAccounts;
	
	@OneToMany(mappedBy="admin", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<InternalChargeAccount> chargeAccounts;
	
	@OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<AccountStatement> accountStatements;
	
	public Admin() {
		super();
		this.setAuthMethod(AuthMethod.ADMIN);
		this.setUserType(UserType.INTERNAL);
		this.setInternalAccounts(new ArrayList<>());
		this.setChargeAccounts(new ArrayList<>());
		this.setAccountStatements(new ArrayList<>());
	}
	
	public List<InternalAccount> getInternalAccounts() {
		return internalAccounts;
	}

	public void setInternalAccounts(List<InternalAccount> internalAccounts) {
		this.internalAccounts = internalAccounts;
	}

	public List<InternalChargeAccount> getChargeAccounts() {
		return chargeAccounts;
	}

	public void setChargeAccounts(List<InternalChargeAccount> chargeAccount) {
		this.chargeAccounts = chargeAccount;
	}
	
	public List<AccountStatement> getAccountStatements() {
		return accountStatements;
	}

	public void setAccountStatements(List<AccountStatement> accountStatements) {
		this.accountStatements = accountStatements;
	}
	
}