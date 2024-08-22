package com.fastcash.moneytransfer.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fastcash.moneytransfer.enums.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public class Account {
	private Long accountNumber;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private Currency currency;
	
	@NotNull
	@CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
	@Version
	@Column(name = "version", nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int version;
	
	public Account() {
		this.createdAt = LocalDateTime.now();
	}
	
	public Account(Currency currency) {
		this();
		this.currency = currency;
	}
	
	public Account(Currency currency, Long accountNumber) {
		this(currency);
		this.accountNumber = accountNumber;
	}
	
	public Long getAccountNumber() {
		return accountNumber;
	}
	
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public Currency getCurrency() {
		return currency;
	}
	
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


}
