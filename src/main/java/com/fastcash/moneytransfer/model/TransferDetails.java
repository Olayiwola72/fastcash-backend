package com.fastcash.moneytransfer.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fastcash.moneytransfer.constant.Constants;
import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@MappedSuperclass
public class TransferDetails {
	
	@Column(nullable = false)
	@NotNull
	private String transactionId;
	
	@DecimalMin(value = "0", inclusive = false)
	@Column(nullable = false, precision = 10, scale = 3)
	@NotNull
	private BigDecimal amount;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Currency debitCurrency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Currency creditCurrency;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "debit_account_id", nullable = false)
	@JsonBackReference
    private UserAccount debitAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_internal_account_id")
    @JsonBackReference
    private UserAccount creditInternalAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credit_external_account_id")
    @JsonBackReference
    private ExternalAccount creditExternalAccount;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internal_account_id")
    @JsonBackReference
	private InternalAccount internalAccount;
	
	@Column(length = 35)
	private String notes;
	
	@NotNull
	@Column(nullable = false, precision=15, scale = 4)
	@DecimalMin(value = "0", inclusive = false)
	private BigDecimal conversionRate;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	@Column(nullable = false, scale = 3)
	private BigDecimal totalDebitedAmount;
	
	@NotNull       
	@DecimalMin(value = "0", inclusive = false)
	@Column(nullable = false, scale = 3)
	private BigDecimal totalCreditedAmount;
	
	@Column(nullable = false, scale = 3)
	private BigDecimal chargeAmount;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internal_charge_account_id")
	private InternalChargeAccount internalChargeAccount;
	
	@NotNull 
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@NotNull
	@CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
	private boolean deleted = Boolean.FALSE;
	
	@Version
	@Column(name = "version", nullable = false)
	private int version;

	public TransferDetails() {
		this.setChargeAmount(BigDecimal.ZERO);
        this.createdAt = LocalDateTime.now(); // Set the default value for createdAt to the current date and time
	}

	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount.setScale(Constants.AMOUNT_SCALE, RoundingMode.HALF_UP);
        this.setTotalDebitedAmount(amount);
        this.setTotalCreditedAmount(amount);
	}
	
	public Currency getDebitCurrency() {
		return debitCurrency;
	}

	public void setDebitCurrency(Currency debitCurrency) {
		this.debitCurrency = debitCurrency;
	}

	public Currency getCreditCurrency() {
		return creditCurrency;
	}

	public void setCreditCurrency(Currency creditCurrency) {
		this.creditCurrency = creditCurrency;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Account getCreditAccount() {
        return creditInternalAccount != null ? creditInternalAccount : creditExternalAccount;
    }

	public void setCreditAccount(UserAccount creditAccount) {
		this.creditInternalAccount = creditAccount;
	}
	
	public void setCreditAccount(ExternalAccount creditAccount) {
		this.creditExternalAccount = creditAccount;
	}
	
	public UserAccount getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(UserAccount debitAccount) {
		this.debitAccount = debitAccount;
	}

	public InternalAccount getInternalAccount() {
		return internalAccount;
	}

	public void setInternalAccount(InternalAccount internalAccount) {
		this.internalAccount = internalAccount;
	}
	
	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public BigDecimal getTotalDebitedAmount() {
		return totalDebitedAmount;
	}
	
	public void setTotalDebitedAmount(BigDecimal totalDebitedAmount) {
		this.totalDebitedAmount = totalDebitedAmount.setScale(Constants.AMOUNT_SCALE, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalCreditedAmount() {
		return totalCreditedAmount;
	}
	
	public void setTotalCreditedAmount(BigDecimal totalCreditedAmount) {
		this.totalCreditedAmount = totalCreditedAmount.setScale(Constants.AMOUNT_SCALE, RoundingMode.HALF_UP);
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount.setScale(Constants.AMOUNT_SCALE, RoundingMode.HALF_UP);
	}
	
	public InternalChargeAccount getInternalChargeAccount() {
		return internalChargeAccount;
	}

	public void setInternalChargeAccount(InternalChargeAccount internalChargeAccount) {
		this.internalChargeAccount = internalChargeAccount;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
