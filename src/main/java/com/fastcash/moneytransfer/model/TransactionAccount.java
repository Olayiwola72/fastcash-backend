package com.fastcash.moneytransfer.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.AccountCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public class TransactionAccount extends Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_account_sequence")
    @SequenceGenerator(name = "transaction_account_sequence", sequenceName = "transaction_account_sequence", allocationSize = 1, initialValue = 10000)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
	
	@NotNull
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "account balance", example = "-40.200")
	@Column(scale = 3)
	private BigDecimal balance;
	
	@NotNull
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "account category", example = "USER")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountCategory accountCategory;
	
	@NotNull
	@Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "allow overdraft", example = "true")
	private boolean allowOverdraft;
	
	public TransactionAccount() {
		super(); // Ensure the default constructor of Account is called
		this.setBalance(BigDecimal.ZERO);
		this.setAllowOverdraft(true);
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public Long getAccountNumber() {
		return super.getAccountNumber() != null ? super.getAccountNumber() : this.getId();
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance.setScale(Constants.BALANCE_SCALE, RoundingMode.HALF_UP);
	}
	
	public AccountCategory getAccountCategory() {
		return accountCategory;
	}

	public void setAccountCategory(AccountCategory accountCategory) {
		this.accountCategory = accountCategory;
	}
	
	public boolean isAllowOverdraft() {
		return allowOverdraft;
	}

	public void setAllowOverdraft(boolean allowOverdraft) {
		this.allowOverdraft = allowOverdraft;
	}
	
}
