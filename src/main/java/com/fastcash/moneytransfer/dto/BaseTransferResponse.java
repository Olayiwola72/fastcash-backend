package com.fastcash.moneytransfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fastcash.moneytransfer.enums.Currency;
import com.fastcash.moneytransfer.enums.TransactionType;
import com.fastcash.moneytransfer.model.Account;
import com.fastcash.moneytransfer.model.AccountStatement;
import com.fastcash.moneytransfer.model.InternalAccount;
import com.fastcash.moneytransfer.model.InternalChargeAccount;
import com.fastcash.moneytransfer.model.MoneyTransfer;
import com.fastcash.moneytransfer.model.UserAccount;
import com.fastcash.moneytransfer.util.DateFormatter;
import com.fastcash.moneytransfer.util.NarrationUtil;

import io.swagger.v3.oas.annotations.media.Schema;

public abstract class BaseTransferResponse {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "record id", example = "1")
    private Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "transaction id", example = "1713717611104-70eed7")
    private String transactionId;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "amount", example = "10.000")
    private BigDecimal amount;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "debit currency", example = "NGN")
    private Currency debitCurrency;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "credit currency", example = "USD")
    private Currency creditCurrency;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "debit account", example = "10002")
    private UserAccount debitAccount;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "notes", example = "this is a test transfer")
    private String notes;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE, description = "credit account", example = "10003")
    private Account creditAccount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "internal account")
    private InternalAccount internalAccount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "currency rate", example = "1000")
    private BigDecimal conversionRate;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "total debited amount", example = "10.000")
    private BigDecimal totalDebitedAmount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "total credited amount", example = "10.000")
    private BigDecimal totalCreditedAmount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "charge amount", example = "0.5")
    private BigDecimal chargeAmount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "internal charge account")
    private InternalChargeAccount internalChargeAccount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "transaction type", example = "OWN_ACCOUNT")
    private TransactionType transactionType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "narration", example = "airtime")
    private String narration;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "creation date and time", example = "2022-04-25T10:15:30")
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "creation date and time formated", example = "2022-04-25T10:15:30")
    private String createdAtFormatted;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "deletion date and time", example = "20 Jun, at 3:47 PM")
    private LocalDateTime deletedAt;

    public BaseTransferResponse populateCommonFields(Object source) {
    	
        if (source instanceof MoneyTransfer) {
            MoneyTransfer moneyTransfer = (MoneyTransfer) source;
            
            this.id = moneyTransfer.getId();
            this.transactionId = moneyTransfer.getTransactionId();
            this.amount = moneyTransfer.getAmount();
            this.debitCurrency = moneyTransfer.getDebitCurrency();
            this.creditCurrency = moneyTransfer.getCreditCurrency();
            this.debitAccount = moneyTransfer.getDebitAccount();
            this.notes = moneyTransfer.getNotes();
            this.creditAccount = moneyTransfer.getCreditAccount();
            this.internalAccount = moneyTransfer.getInternalAccount();
            this.conversionRate = moneyTransfer.getConversionRate();
            this.totalDebitedAmount = moneyTransfer.getTotalDebitedAmount();
            this.totalCreditedAmount = moneyTransfer.getTotalCreditedAmount();
            this.chargeAmount = moneyTransfer.getChargeAmount();
            this.internalChargeAccount = moneyTransfer.getInternalChargeAccount();
            this.transactionType = moneyTransfer.getTransactionType();
            this.narration = NarrationUtil.getNarration(moneyTransfer.getTransactionType(), moneyTransfer.getNotes());
            this.createdAt = moneyTransfer.getCreatedAt();
            this.createdAtFormatted = DateFormatter.formatDate(moneyTransfer.getCreatedAt());
        } else if (source instanceof AccountStatement) {
            AccountStatement accountStatement = (AccountStatement) source;
            
            this.id = accountStatement.getId();
            this.transactionId = accountStatement.getTransactionId();
            this.amount = accountStatement.getAmount();
            this.debitCurrency = accountStatement.getDebitCurrency();
            this.creditCurrency = accountStatement.getCreditCurrency();
            this.debitAccount = accountStatement.getDebitAccount();
            this.notes = accountStatement.getNotes();
            this.creditAccount = accountStatement.getCreditAccount();
            this.internalAccount = accountStatement.getInternalAccount();
            this.conversionRate = accountStatement.getConversionRate();
            this.totalDebitedAmount = accountStatement.getTotalDebitedAmount();
            this.totalCreditedAmount = accountStatement.getTotalCreditedAmount();
            this.chargeAmount = accountStatement.getChargeAmount();
            this.internalChargeAccount = accountStatement.getInternalChargeAccount();
            this.transactionType = accountStatement.getTransactionType();
            this.narration = NarrationUtil.getNarration(accountStatement.getTransactionType(), accountStatement.getNotes());
            this.createdAt = accountStatement.getCreatedAt();
            this.createdAtFormatted = DateFormatter.formatDate(accountStatement.getCreatedAt());
        }
        
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getDebitCurrency() {
        return debitCurrency;
    }

    public Currency getCreditCurrency() {
        return creditCurrency;
    }

    public UserAccount getDebitAccount() {
        return debitAccount;
    }

    public String getNotes() {
        return notes;
    }

    public Account getCreditAccount() {
        return creditAccount;
    }

    public InternalAccount getInternalAccount() {
        return internalAccount;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public BigDecimal getTotalDebitedAmount() {
        return totalDebitedAmount;
    }

    public BigDecimal getTotalCreditedAmount() {
        return totalCreditedAmount;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public InternalChargeAccount getInternalChargeAccount() {
        return internalChargeAccount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
    
	public String getNarration() {
		return narration;
	}
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
}
